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
package com.krawler.portal.tools;

import antlr.CharBuffer;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONException;
import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;
import de.hunsicker.jalopy.storage.Environment;


import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

import com.krawler.portal.util.GetterUtil;
import com.krawler.portal.util.Time;
import com.krawler.portal.util.Validator;
import com.krawler.portal.util.XMLFormatter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.*;
import org.dom4j.DocumentException;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.SAXException;
import com.krawler.esp.utils.PropsValues;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.workflow.module.bizservice.DataObjectOperations;
import com.krawler.workflow.module.dao.ModuleProperty;
import java.net.URL;
import java.sql.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

public class ServiceBuilder {
    private String _hbmFileName;
//    private static final String _TPL_ROOT ="com/krawler/portal/tools/dependencies/";
//    private String _tplModel = _TPL_ROOT + "model.ftl";
//    private String _tplHbmXml = _TPL_ROOT + "hbm_xml.ftl";
//    private String _packagePath = "com.krawler.esp.hibernate.impl";
////    private String generateDirPath = "/home/mosin/store/KWLTools/";
//    private String generateDirPath = "/home/mosin/NetBeansProjects/HibernateFramework/src/java/com/krawler/esp/hibernate/impl/";
//    private String cfgFilePath = "/home/mosin/NetBeansProjects/HibernateFramework/src/java/hibernate.cfg.xml";
//    private String packagePath = "com/krawler/esp/hibernate/impl/";
    private List<Entity> _ejbList;
    private String _table;
    private static final Log logger  = LogFactory.getLog(ServiceBuilder.class);
    public void createBusinessProcessforCRUD(String classname,String companyid){
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ClassPathResource("logic/businesslogicEx.xml").getFile());
            //Document doc = docBuilder.newDocument();
            NodeList businessrules = doc.getElementsByTagName("businessrules");
            Node businessruleNode = businessrules.item(0);
            Element processEx =  doc.getElementById(classname+"_addNew");
            
            if(processEx != null){
                businessruleNode.removeChild(processEx);
            }
            
            processEx =  doc.getElementById(classname+"_delete");
            
            if(processEx != null){
                businessruleNode.removeChild(processEx);
            }
            processEx =  doc.getElementById(classname+"_edit");
            if(processEx != null){
                businessruleNode.removeChild(processEx);
            }
            
            Element process = createBasicProcessNode(doc, classname, "createNewRecord", "_addNew", "createNew");
                       businessruleNode.appendChild(process);
            process = createBasicProcessNode(doc, classname, "deleteRecord", "_delete", "deleteRec");
                       businessruleNode.appendChild(process);
            process = createBasicProcessNode(doc, classname, "editRecord", "_edit", "editRec");
           businessruleNode.appendChild(process);

            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//KRAWLER//DTD BUSINESSRULES//EN");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://192.168.0.4/dtds/businesslogicEx.dtd");
            trans.setOutputProperty(OutputKeys.VERSION,"1.0");
            trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");

            // create string from xml tree
            File outputFile  = (new ClassPathResource("logic/businesslogicEx.xml").getFile());
            outputFile.setWritable(true);
           // StringWriter sw = new StringWriter();
            StreamResult sresult = new StreamResult(outputFile);

            DOMSource source = new DOMSource(doc);
            trans.transform(source, sresult);


        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }



    }

    public Element createBasicProcessNode(Document doc,String className,String operation,String oppSuffix,String oppPrefix){
         Element process = doc.createElement("process");
            process.setAttribute("id", className+oppSuffix);
            process.setAttribute("init", oppPrefix+className);

            Element vars = doc.createElement("vars");

            Element in_var = doc.createElement("in-var");
            in_var.setAttribute("name", "moduleObj");
            in_var.setAttribute("module", className);
            Element local_var = doc.createElement("local-var");
            local_var.setAttribute("name", "retobj");
            local_var.setAttribute("module", "string");
            vars.appendChild(in_var);
            vars.appendChild(local_var);
            process.appendChild(vars);
            Element out_var = doc.createElement("out-var");
            out_var.setAttribute("name", "res");
            process.appendChild(out_var);
            Element node_list = doc.createElement("node-list");
                Element node = doc.createElement("node");
                node.setAttribute("id", oppPrefix+className);
                node.setAttribute("invoke",operation);
                Element output = doc.createElement("output");
                output.appendChild(doc.createTextNode("retObj"));
                Element inputs = doc.createElement("inputs");
                Element var = doc.createElement("var");
                var.setAttribute("name", "moduleObj");
                var.appendChild(doc.createTextNode("moduleObj"));
                inputs.appendChild(var);
                node.appendChild(output);
                node.appendChild(inputs);
                node_list.appendChild(node);

                process.appendChild(node_list);
        return process;
    }
    public void createModuleDef(com.krawler.utils.json.base.JSONArray jsonData,String classname){
        String result = "";
        try {

            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

            Document doc = docBuilder.parse((new ClassPathResource("logic/moduleEx.xml").getFile()));
            //Document doc = docBuilder.newDocument();
            NodeList modules = doc.getElementsByTagName("modules");
            Node modulesNode = modules.item(0);
           Element module_ex = doc.getElementById(classname);
            if(module_ex != null){
                modulesNode.removeChild(module_ex);
            }
            Element module = doc.createElement("module");
            Element property_list = doc.createElement("property-list");
           module.setAttribute("class", "com.krawler.esp.hibernate.impl."+classname);
            module.setAttribute("type", "pojo");
            module.setAttribute("id", classname);
            for (int cnt = 0; cnt < jsonData.length(); cnt++) {
                Element propertyNode = doc.createElement("property");
                JSONObject jsonObj = jsonData.optJSONObject(cnt);


                propertyNode.setAttribute("name", jsonObj.optString("varname"));
                propertyNode.setAttribute("type", jsonObj.optString("modulename").toLowerCase());
                property_list.appendChild(propertyNode);


            }

            module.appendChild(property_list);
            modulesNode.appendChild(module);
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//KRAWLER//DTD BUSINESSRULES//EN");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://localhost/dtds/module.dtd");
            trans.setOutputProperty(OutputKeys.VERSION,"1.0");
            trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            // create string from xml tree
            File outputFile  = (new ClassPathResource("logic/moduleEx.xml").getFile());
            outputFile.setWritable(true);
           // StringWriter sw = new StringWriter();
            StreamResult sresult = new StreamResult(outputFile);

            DOMSource source = new DOMSource(doc);
            trans.transform(source, sresult);
     //       result  = sw.toString();


        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public void createModuleDef(ArrayList list,String classname){
        String result = "";
        try {
            
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

            Document doc = docBuilder.parse((new ClassPathResource("logic/moduleEx.xml").getFile()));
            //Document doc = docBuilder.newDocument();
            NodeList modules = doc.getElementsByTagName("modules");
            Node modulesNode = modules.item(0);
           Element module_ex = doc.getElementById(classname);
            if(module_ex != null){
                modulesNode.removeChild(module_ex);
            }
            Element module = doc.createElement("module");
            Element property_list = doc.createElement("property-list");
           module.setAttribute("class", "com.krawler.esp.hibernate.impl."+classname);
            module.setAttribute("type", "pojo");
            module.setAttribute("id", classname);
            for (int cnt = 0; cnt < list.size(); cnt++) {
                Element propertyNode = doc.createElement("property");
                Hashtable mapObj = (Hashtable)list.get(cnt);

            
                propertyNode.setAttribute("name", mapObj.get("name").toString());
                propertyNode.setAttribute("type", mapObj.get("type").toString().toLowerCase());
                property_list.appendChild(propertyNode);
                

            }

            module.appendChild(property_list);
            modulesNode.appendChild(module);
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//KRAWLER//DTD BUSINESSRULES//EN");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://192.168.0.4/dtds/module.dtd");
            trans.setOutputProperty(OutputKeys.VERSION,"1.0");
            trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            // create string from xml tree
            File outputFile  = (new ClassPathResource("logic/moduleEx.xml").getFile());
            outputFile.setWritable(true);
           // StringWriter sw = new StringWriter();
            StreamResult sresult = new StreamResult(outputFile);

            DOMSource source = new DOMSource(doc);
            trans.transform(source, sresult);
     //       result  = sw.toString();
            
            
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }finally{
           // System.out.println(result);
           // return result;
        }
    }

          public void createServiceXMLFile(com.krawler.utils.json.base.JSONArray jsonData,String tableName){
         try{
            String filename = PropsValues.GENERATE_DIR_PATH+"service.xml";
            File destPath = new File(filename);
            destPath.createNewFile();
            // Creation of an XML document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            DOMImplementation di = db.getDOMImplementation();
            Document doc = di.createDocument(null, "service-builder", null);
            Element root = doc.getDocumentElement();
            root.setAttribute("package-path","com.krawler.esp.hibernate.impl");

            Element classEl = doc.createElement("entity");
            classEl.setAttribute("name",tableName);
            classEl.setAttribute("local-service","true");
            classEl.setAttribute("remote-service","true");
            root.appendChild(classEl);
            Element el = null;

            for(int i = 0; i < jsonData.length(); i++){
                el = doc.createElement("column");

                JSONObject jobj = jsonData.optJSONObject(i);
                el.setAttribute("name",jobj.optString("varname"));
                el.setAttribute("type",jobj.optString("modulename"));
//                if(!jsonData.get(i).get("default").toString().equals("")){
//                    el.setAttribute("default",columnInfo.get(i).get("default").toString());
//                }
                if(jobj.has("primaryid")){
                    el.setAttribute("primary","true");
                }
                if(jobj.has("foreignid")){
                    el.setAttribute("foreign","true");
                    el.setAttribute("class", jobj.get("reftable").toString());
                }
                classEl.appendChild(el);
            }
            // output of the XML document
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(destPath);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Mapping DTD 3.0//EN");
            trans.transform(ds, sr);
         } catch(Exception e) {
            logger.warn(e.getMessage(), e);
         }
     }
            public void createServiceTableEntry(DataObjectOperations dobj, ArrayList<Hashtable<String, Object>> columnInfo,String tableName,String moduleid){
         try{
          
       
           
            JSONObject jobj = new JSONObject();
            jobj.put("tablename", tableName);
            String primaryField = "id";
            Element el = null;
            int count = columnInfo.size()-1;
            for(int i = count; i >= 0; i--){
                JSONObject jobjTemp = new JSONObject();
                jobjTemp.put("name", columnInfo.get(i).get("name").toString());
                jobjTemp.put("type",columnInfo.get(i).get("type").toString());
                
                if(!columnInfo.get(i).get("default").toString().equals("")){
                    jobjTemp.put("default",columnInfo.get(i).get("default").toString());
                }
                if(columnInfo.get(i).containsKey("primaryid")){
                    jobjTemp.put("primaryid",true);
                    primaryField = columnInfo.get(i).get("name").toString();

                }
                if(columnInfo.get(i).containsKey("foreignid") && Boolean.parseBoolean(columnInfo.get(i).get("foreignid").toString())){
                    jobjTemp.put("foreignid",true);
                    jobjTemp.put("table", columnInfo.get(i).get("reftable").toString());
                    jobjTemp.put("reffield", columnInfo.get(i).get("reffield"));

                }
               jobj.append("data", jobjTemp.toString());
            }
            //Save in datanase
            HashMap mapObj = new HashMap();
            mapObj.put("fieldData", jobj.toString());
            mapObj.put("moduleid",moduleid );
            mapObj.put("primaryField", primaryField);
            dobj.deleteDataObject("mbFormConfig", "moduleid", moduleid);
            dobj.createDataObject("mbFormConfig",mapObj);
         } catch(Exception e) {
            logger.warn(e.getMessage(), e);
         }
     }
      public void createServiceXMLFile(ArrayList<Hashtable<String, Object>> columnInfo,String tableName){
         try{
            String filename = PropsValues.GENERATE_DIR_PATH+"service.xml";
            File destPath = new File(filename);
            destPath.createNewFile();
            // Creation of an XML document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            DOMImplementation di = db.getDOMImplementation();
            Document doc = di.createDocument(null, "service-builder", null);
            Element root = doc.getDocumentElement();
            root.setAttribute("package-path","com.krawler.esp.hibernate.impl");

            Element classEl = doc.createElement("entity");
            classEl.setAttribute("name",tableName);
            classEl.setAttribute("local-service","true");
            classEl.setAttribute("remote-service","true");
            root.appendChild(classEl);
            Element el = null;
            int count = columnInfo.size()-1;
            for(int i = count; i >= 0; i--){
                el = doc.createElement("column");
                el.setAttribute("name",columnInfo.get(i).get("name").toString());
                el.setAttribute("type",columnInfo.get(i).get("type").toString());
                if(!columnInfo.get(i).get("default").toString().equals("")){
                    el.setAttribute("default",columnInfo.get(i).get("default").toString());
                }
                if(columnInfo.get(i).containsKey("primaryid")){
                    el.setAttribute("primary","true");
                }
                if(columnInfo.get(i).containsKey("foreignid") && Boolean.parseBoolean(columnInfo.get(i).get("foreignid").toString())){
                    el.setAttribute("foreign","true");
                    el.setAttribute("class", columnInfo.get(i).get("reftable").toString());
                }
                classEl.appendChild(el);
            }
            // output of the XML document
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(destPath);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Mapping DTD 3.0//EN");
            trans.transform(ds, sr);
         } catch(Exception e) {
            logger.warn(e.getMessage(), e);
         }
     }
    public void createJavaFile(String tableName, boolean reportFlag) {
        try {
            File f1 = new File(PropsValues.GENERATE_DIR_PATH + "service.xml");
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(f1);
            NodeList entity = doc.getElementsByTagName("entity");
            List<EntityColumn> columnList = new ArrayList<EntityColumn>();
            List<EntityColumn> regularColList = new ArrayList<EntityColumn>();
            List<EntityColumn> pkList = new ArrayList<EntityColumn>();
            List<EntityColumn> fkList = new ArrayList<EntityColumn>();
            NodeList columns = ((Element)entity.item(0)).getElementsByTagName("column");//.item(0);
            for(int i=0;i<columns.getLength();i++){
                Element columnElement = (Element) columns.item(i);
                boolean primary =columnElement.hasAttribute("primary")?true:false;
                boolean foreign =columnElement.hasAttribute("foreign")?true:false;
                String columnName = columnElement.getAttribute("name");
                String defaultValue = columnElement.hasAttribute("default")?columnElement.getAttribute("default"):"";
                String columnType="";
                if(foreign){
                    columnType = columnElement.getAttribute("class");
                }else{
                    columnType = columnElement.getAttribute("type");
                }
                boolean convertNull = GetterUtil.getBoolean(null, true);
                EntityColumn col = new EntityColumn(columnName, columnType, primary, foreign, convertNull, defaultValue);
                if(primary){
                    pkList.add(col);
                }
                if(foreign){
                    fkList.add(col);
                }//else{
                     regularColList.add(col);
//                }
                 columnList.add(col);
            }
            _ejbList = new ArrayList<Entity>();
            String ejbName = tableName; // entity-> name
            String table = "" ;// entity-> table
            if(StringUtil.isNullOrEmpty(table)) {
                table = ejbName;
            }
            // iterate for columns
            String packagePath = PropsValues.PACKAGE_PATH;
            Entity entityObj = new Entity(packagePath,ejbName,table,regularColList,columnList,pkList,fkList);
            _ejbList.add(entityObj);
            if (entityObj.hasColumns()) {
                _createModel(entityObj);
            }

            packagePath = PropsValues.PACKAGE_PATH;
            boolean createTableFlg = true;
            Entity entityObj1 = new Entity(packagePath,"impl_"+ejbName, reportFlag, ejbName, createTableFlg);
            _ejbList.add(entityObj1);
            _createModelImpl(entityObj1);

            //Check before adding a cfg entry in _createHbmXml()
            boolean flag = isModuleNew(ejbName);
            if(!flag){
                deleteClassesModuleStuf(ejbName);
            }
            _createHbmXml(ejbName);
//            _setModuleProperties(ejbName);
            //_buildModule();

            String resourceName = "";
            if(flag) {
                resourceName = PropsValues.PACKAGE_FILE_PATH+ejbName+".hbm.xml";
            }
//            writeToClassesCfgXml(ejbName);
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public void createImplJavaFile(String tableName, boolean reportFlag) {
        try {
            _ejbList = new ArrayList<Entity>();
            String ejbName = tableName; // entity-> name
            String table = "" ;// entity-> table
            if(StringUtil.isNullOrEmpty(table)) {
                table = ejbName;
            }
            // iterate for columns
            String packagePath = PropsValues.PACKAGE_PATH;
            boolean createTableFlg = false;
            Entity entityObj1 = new Entity(packagePath,"impl_"+ejbName, reportFlag, ejbName, createTableFlg);
            _ejbList.add(entityObj1);
            _createModelImpl(entityObj1);

//            _setModuleProperties(ejbName);
//            _buildModule();

        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

     public static void writeFile(
			File file, String content, String author,
			Map<String, Object> jalopySettings)
		throws IOException {

		String packagePath ="com/krawler/esp/hibernate/impl";//_getPackagePath(file);

		String className = file.getName();

		className = className.substring(0, className.length() - 5);

		content = SourceFormatter.stripImports(content, packagePath, className);

		File tempFile = new File("ServiceBuilder.temp");
                Writer output = new BufferedWriter(new FileWriter(tempFile));
                output.write(content);
                output.close();
		StringBuffer sb = new StringBuffer();
		Jalopy jalopy = new Jalopy();
		jalopy.setFileFormat(FileFormat.UNIX);
		jalopy.setInput(tempFile);
		jalopy.setOutput(sb);
//		try {
//			Jalopy.setConvention("../tools/jalopy.xml");
//		}
//		catch (FileNotFoundException fnne) {
//                    System.out.print(fnne.getMessage());
//		}
//		try {
//			Jalopy.setConvention("../../misc/jalopy.xml");
//		}
//		catch (FileNotFoundException fnne) {
//                    System.out.print(fnne.getMessage());
//		}
		if (jalopySettings == null) {
			jalopySettings = new HashMap<String, Object>();
		}

		Environment env = Environment.getInstance();

		// Author

		author = GetterUtil.getString(
			(String)jalopySettings.get("author"), author);

		env.set("author", author);

		// File name

		env.set("fileName", file.getName());

		Convention convention = Convention.getInstance();

		String classMask =
			"/**\n" +
			" * <a href=\"$fileName$.html\"><b><i>View Source</i></b></a>\n" +
			" *\n" +
			" * @author $author$\n" +
			" *\n" +
			"*/";

		convention.put(
			ConventionKeys.COMMENT_JAVADOC_TEMPLATE_CLASS,
			env.interpolate(classMask));

		convention.put(
			ConventionKeys.COMMENT_JAVADOC_TEMPLATE_INTERFACE,
			env.interpolate(classMask));

		jalopy.format();

		String newContent = sb.toString();

		/*
		// Remove blank lines after try {

		newContent = StringUtil.replace(newContent, "try {\n\n", "try {\n");

		// Remove blank lines after ) {

		newContent = StringUtil.replace(newContent, ") {\n\n", ") {\n");

		// Remove blank lines empty braces { }

		newContent = StringUtil.replace(newContent, "\n\n\t}", "\n\t}");

		// Add space to last }

		newContent = newContent.substring(0, newContent.length() - 2) + "\n\n}";
		*/

		// Write file if and only if the file has changed

		String oldContent = null;

		if (file.exists()) {

			// Read file
                        Reader reader = new BufferedReader(new FileReader(file));
                        CharBuffer cbuf = new CharBuffer(reader);
			oldContent = cbuf.toString();
                        reader.close();

			// Keep old version number

			int x = oldContent.indexOf("@version $Revision:");

			if (x != -1) {
				int y = oldContent.indexOf("$", x);
				y = oldContent.indexOf("$", y + 1);

				String oldVersion = oldContent.substring(x, y + 1);

				newContent = com.krawler.portal.util.StringUtil.replace(
					newContent, "@version $Rev: $", oldVersion);
			}
		}
		else {
//			newContent = com.krawler.portal.util.StringUtil.replace(
//				newContent, "@version $Rev: $", "@version $Revision: 1.183 $");
                        file.createNewFile();
		}

		if (oldContent == null || !oldContent.equals(newContent)) {
                        output = new BufferedWriter(new FileWriter(file));
                        output.write(content);
                        output.close();
//			FileUtil.write(file, newContent);

			System.out.println("Writing " + file);

			// Workaround for bug with XJavaDoc

			file.setLastModified(
				System.currentTimeMillis() - (Time.SECOND * 5));
		}

		tempFile.deleteOnExit();
	}


    private void _createModel(Entity entity) throws Exception {
            Map<String, Object> context = _getContext();

            context.put("entity", entity);

            // Content

            String content = _processTemplate(PropsValues.TPL_MODEL, context);

            // Write file

//            File modelFile = new File(_packagePath+"/" + entity.getName() + "Model.java");
            File modelFile = new File(PropsValues.GENERATE_DIR_PATH + entity.getName() + ".java");

            Map<String, Object> jalopySettings = new HashMap<String, Object>();

            jalopySettings.put("keepJavadoc", Boolean.TRUE);

            writeFile(modelFile, content, "sm@krawler", jalopySettings);
    }

    private void _createModelImpl(Entity entity) throws Exception {
            Map<String, Object> context = _getContext();

            context.put("entity", entity);

            // Content
            String content = _processTemplate(PropsValues.TPL_IMPL_MODEL, context);
            // Write file

//            File modelFile = new File(_packagePath+"/" + entity.getName() + "Model.java");
            File modelFile = new File(PropsValues.GENERATE_DIR_PATH +  entity.getName() + ".java");

            Map<String, Object> jalopySettings = new HashMap<String, Object>();

            jalopySettings.put("keepJavadoc", Boolean.TRUE);

            writeFile(modelFile, content, "sm@krawler", jalopySettings);
    }

    private boolean isModuleNew(String ejbName) throws Exception {
        _hbmFileName = PropsValues.GENERATE_DIR_PATH + ejbName + ".hbm.xml";
        File xmlFile = new File(_hbmFileName);
        if (xmlFile.exists()) {
            return false;
        } else {
            return true;
        }
   }

   private void _createHbmXml(String ejbName) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entities", _ejbList);

		// Content

		String content = _processTemplate(PropsValues.TPL_HBM_XML, context);

                _hbmFileName = PropsValues.GENERATE_DIR_PATH +ejbName+".hbm.xml";
		File xmlFile = new File(_hbmFileName);
        if (xmlFile.exists()) {
            xmlFile.delete();
        }else{
              writeToSourceCfgXml(ejbName);
        }
//		if (!xmlFile.exists()) {
			String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"\n" +
				"<hibernate-mapping default-lazy=\"false\" auto-import=\"false\">\n" +
				"</hibernate-mapping>";

//			FileUtil.write(xmlFile, xml);
                        Writer output = new BufferedWriter(new FileWriter(xmlFile));
                        output.write(xml);
                        output.close();
//		}
//                String oldContent = FileUtil.read(xmlFile);

                String oldContent = convertXMLFileToString(xmlFile);

		String newContent = _fixHbmXml(oldContent);

		int firstClass = newContent.indexOf(
			"<class name=\"" + PropsValues.PACKAGE_PATH + ".");
		int lastClass = newContent.lastIndexOf(
			"<class name=\"" + PropsValues.PACKAGE_PATH + ".");

		if (firstClass == -1) {
			int x = newContent.indexOf("</hibernate-mapping>");

			if (x != -1) {
				newContent =
					newContent.substring(0, x) + content +
					newContent.substring(x, newContent.length());
			}
		}
		else {
			firstClass = newContent.lastIndexOf("<class", firstClass) - 1;
			lastClass = newContent.indexOf("</class>", lastClass) + 9;

			newContent =
				newContent.substring(0, firstClass) + content +
					newContent.substring(lastClass, newContent.length());
		}

//		newContent = _formatXml(newContent);
    /* Writer */output = new BufferedWriter(new FileWriter(xmlFile));
                output.write(newContent);
                output.close();
		if (!oldContent.equals(newContent)) {
           output = new BufferedWriter(new FileWriter(xmlFile));
           output.write(newContent);
           output.close();
//    System.out.println("Your file has been written");
//            FileUtil.write(xmlFile, newContent);
		}

	}
    private void _setModuleProperties(String entity) throws Exception {
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            bw = new BufferedWriter(new FileWriter(PropsValues.MODULE_PROPERTIES));
            pw = new PrintWriter(bw);
            pw.println("module-source-dir=" + PropsValues.MODULE_SOURCE_DIR);
            pw.println("module-classes-dir=" + PropsValues.MODULE_CLASSES_DIR);
            pw.println("module-classes-dest-dir=" + PropsValues.MODULE_CLASSES_DESC_DIR);
            pw.println("module-name=" + entity);
            pw.println("impl-module-name=" + "impl_"+entity);
            pw.println("tomcat-lib=" + PropsValues.TOMCAT_LIB);
            pw.println("project-lib=" + PropsValues.PROJECT_LIB);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (bw != null) {
                bw.close();
            }
            if (pw != null) {
                pw.close();
            }
        }
    }
    
    public void _buildModule(DataObjectOperations dobj, String tablename, String moduleid) {
        Map mapObj = (Map) dobj.getDataObjectMapById("mbFormConfig", "moduleid", moduleid);
        String tableName = null;
        List<ModuleProperty> fieldData = new ArrayList<ModuleProperty>();
        JSONArray jarray = null;
        if (mapObj != null) {
            try {
                JSONObject jobj = new JSONObject(mapObj.get("fielddata").toString());
                tableName = jobj.getString("tablename");
                jarray = jobj.getJSONArray("data");
            } catch (JSONException ex) {
                logger.warn(ex.getMessage(), ex);
                throw new RuntimeException("Module Configuration not found");
            }
        }
        if (jarray.length() == 0) {
            throw new RuntimeException("Module Configuration is incorrect");
        }

        try {
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jobjtemp = new JSONObject(jarray.get(i).toString());
                String type = jobjtemp.getString("type");
                int fieldType = 1;
                if (type.equalsIgnoreCase("String")) {
                    fieldType = Types.VARCHAR;
                } else if (type.equalsIgnoreCase("double")) {
                    fieldType = Types.DOUBLE;
                } else if (type.equals("boolean")) {
                    fieldType = Types.BIT;
                } else if (type.equalsIgnoreCase("date")) {
                    fieldType = Types.DATE;
                } else if (type.equals("int")) {
                    fieldType = Types.INTEGER;
                }
                ModuleProperty moduleField = new ModuleProperty(jobjtemp.get("name").toString(), fieldType);
                moduleField.setForeignKey(jobjtemp.optBoolean("foreignid", false));
                moduleField.setPrimaryKey(jobjtemp.optBoolean("primaryid", false));
                moduleField.setDefaultValue(jobjtemp.optString("defaults", ""));
                moduleField.setRefTable(jobjtemp.optString("table", ""));
                moduleField.setRefField(jobjtemp.optString("reffield", ""));
                fieldData.add(moduleField);
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException("Module Configuration is incorrect");
        }catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException("Module Configuration is incorrect");
        }

        dobj.createModuleTable(tableName, fieldData);
    }

    public void _buildModule(String tablename) throws Exception {
        BufferedReader br = null;
        try {
            try{
               String anthome =  System.getenv("ANT_HOME");
               String antpath = "ant ";
               if(anthome != null ){
                   antpath=anthome+"bin\\ant.bat ";
               }
            String command = antpath+"  buildmyjar -Dmodulename="+tablename;
            File buildXMLFile = new File(PropsValues.PROJECT_HOME);
            
//            Process process = Runtime.getRuntime().exec(command);
            Process process = Runtime.getRuntime().exec(command, null, buildXMLFile);
            // Get the input stream and read from it
            
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null, output = "";
            while ((line = br.readLine()) != null) {
                output += line + "\n";
            }
            
            System.out.print(output);
            process.destroy();
            }catch(IOException ex){
                 logger.warn(ex.getMessage(), ex);
            }
            com.krawler.runtime.utils.URLClassLoaderUtil urlObj = new com.krawler.runtime.utils.URLClassLoaderUtil(new URL[]{});
            File jarFile = new File(PropsValues.PROJECT_HOME+"lib\\module-"+tablename+".jar");
            urlObj.addFile(jarFile);
            
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void writeToSourceCfgXml(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(PropsValues.CFG_SOURCE_FILE_PATH);
            Node hibernate_conf = doc.getChildNodes().item(1);
//            .getChildNodes().item(1);
            Node SessionFac = hibernate_conf.getChildNodes().item(1);
            Element mapping = doc.createElement("mapping");
            mapping.setAttribute("resource", PropsValues.PACKAGE_FILE_PATH + fileName + ".hbm.xml");
            SessionFac.getChildNodes().getLength();
            SessionFac.appendChild(mapping);
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(PropsValues.CFG_SOURCE_FILE_PATH);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.VERSION, "1.0");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Configuration DTD 3.0//EN");
            trans.transform(ds, sr);
//            writeToClassesCfgXml(fileName);
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public void writeToClassesCfgXml(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.parse(PropsValues.CFG_SOURCE_FILE_PATH);
            Document doc = docBuilder.parse(PropsValues.CFG_CLASSES_FILE_PATH);
            Node hibernate_conf = doc.getChildNodes().item(1);
//            .getChildNodes().item(1);
            Node SessionFac = hibernate_conf.getChildNodes().item(1);
            Element mapping = doc.createElement("mapping");
            mapping.setAttribute("resource", PropsValues.PACKAGE_FILE_PATH + fileName + ".hbm.xml");
            SessionFac.getChildNodes().getLength();
            SessionFac.appendChild(mapping);
            DOMSource ds = new DOMSource(doc);
//            StreamResult sr = new StreamResult(PropsValues.CFG_SOURCE_FILE_PATH);
            StreamResult sr = new StreamResult(PropsValues.CFG_CLASSES_FILE_PATH);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.VERSION, "1.0");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Configuration DTD 3.0//EN");
            trans.transform(ds, sr);

        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }

    }

    public void deleteSourceEntryCfgXml(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(PropsValues.CFG_SOURCE_FILE_PATH);
            Node hibernate_conf = doc.getChildNodes().item(1);
            Node SessionFac = hibernate_conf.getChildNodes().item(1);
            Element sesFac = (Element) SessionFac;
            NodeList mapping_lists = sesFac.getElementsByTagName("mapping");
            Node toDelete = null;
            for (int num = 0; num < mapping_lists.getLength(); num++) {
                Element mapEle = (Element) mapping_lists.item(num);
                if (mapEle.getAttribute("resource").equals(PropsValues.PACKAGE_FILE_PATH + fileName + ".hbm.xml")) {
                    toDelete = mapEle;
                    break;
                }
            }
            sesFac.removeChild(toDelete);
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(PropsValues.CFG_SOURCE_FILE_PATH);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.VERSION, "1.0");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Configuration DTD 3.0//EN");
            trans.transform(ds, sr);
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public void deleteClassesEntryCfgXml(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(PropsValues.CFG_CLASSES_FILE_PATH);
            Node hibernate_conf = doc.getChildNodes().item(1);
            Node SessionFac = hibernate_conf.getChildNodes().item(1);
            Element sesFac = (Element) SessionFac;
            NodeList mapping_lists = sesFac.getElementsByTagName("mapping");
            Node toDelete = null;
            for (int num = 0; num < mapping_lists.getLength(); num++) {
                Element mapEle = (Element) mapping_lists.item(num);
                if (mapEle.getAttribute("resource").equals(PropsValues.PACKAGE_FILE_PATH + fileName + ".hbm.xml")) {
                    toDelete = mapEle;
                    break;
                }
            }
            sesFac.removeChild(toDelete);
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(PropsValues.CFG_CLASSES_FILE_PATH);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.VERSION, "1.0");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Configuration DTD 3.0//EN");
            trans.transform(ds, sr);
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }
   public void deleteModuleStuf(String className){
      String classFileName = PropsValues.GENERATE_DIR_PATH+className+".java";
      String implclassFileName = PropsValues.GENERATE_DIR_PATH+"impl_"+className+".java";
      String xmlFileName = PropsValues.GENERATE_DIR_PATH+className+".hbm.xml";
      File xmlFile = new File(xmlFileName);
      File classFile = new File(classFileName);
      File implclassFile = new File(implclassFileName);
      if(xmlFile.exists()){
          xmlFile.delete();
          deleteSourceEntryCfgXml(className);
      }
      if(classFile.exists()){
          classFile.delete();
      }
      if(implclassFile.exists()){
          implclassFile.delete();
      }
      deleteClassesModuleStuf(className);
   }
    public void deleteClassesModuleStuf(String className) {
        String classFileName = PropsValues.MODULE_CLASSES_DIR + className + ".class";
        String implclassFileName = PropsValues.MODULE_CLASSES_DIR + "impl_"+className + ".class";
        String xmlFileName = PropsValues.MODULE_CLASSES_DIR + className + ".hbm.xml";
        File xmlFile = new File(xmlFileName);
        File classFile = new File(classFileName);
        File implclassFile = new File(implclassFileName);
        if (xmlFile.exists()) {
            xmlFile.delete();
            deleteClassesEntryCfgXml(className);
        }
        if (classFile.exists()) {
            classFile.delete();
        }
        if (implclassFile.exists()) {
            implclassFile.delete();
        }
    }
   public String convertXMLFileToString(File xmlFile)
        {
          try{
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            InputStream inputStream = new FileInputStream(fileName);
//            Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
//            StringWriter stw = new StringWriter();
//            Transformer serializer = TransformerFactory.newInstance().newTransformer();
//            serializer.transform(new DOMSource(doc), new StreamResult(stw));
//            return stw.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(xmlFile);
                DOMSource domSource = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Mapping DTD 3.0//EN");
                transformer.transform(domSource, result);
                return writer.toString();
          }
          catch (Exception e) {
            logger.warn(e.getMessage(), e);
          }
            return null;
        }
    private String _fixHbmXml(String content) throws IOException {
		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new StringReader(content));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.startsWith("\t<class name=\"")) {
				line = com.krawler.portal.util.StringUtil.replace(
					line,
					new String[] {
						".service.persistence.", "HBM\" table=\""
					},
					new String[] {
						".model.", "\" table=\""
					});

				if (line.indexOf(".model.impl.") == -1) {
					line = com.krawler.portal.util.StringUtil.replace(
						line,
						new String[] {
							".model.", "\" table=\""
						},
						new String[] {
							".model.impl.", "Impl\" table=\""
						});
				}
			}

			sb.append(line);
			sb.append('\n');
		}

		br.close();

		return sb.toString().trim();
	}

    private String _formatXml(String xml)
		throws DocumentException, IOException {

		String doctype = null;

		int x = xml.indexOf("<!DOCTYPE");

		if (x != -1) {
			int y = xml.indexOf(">", x) + 1;

			doctype = xml.substring(x, y);

			xml = xml.substring(0, x) + "\n" + xml.substring(y);
		}

		xml =com.krawler.portal.util.StringUtil.replace(xml, '\r', "");
		xml = XMLFormatter.toString(xml);
		xml = com.krawler.portal.util.StringUtil.replace(xml, "\"/>", "\" />");

		if (Validator.isNotNull(doctype)) {
			x = xml.indexOf("?>") + 2;

			xml = xml.substring(0, x) + "\n" + doctype + xml.substring(x);
		}

		return xml;
	}
    private Map<String, Object> _getContext() throws TemplateModelException {
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();

            Map<String, Object> context = new HashMap<String, Object>();
            context.put("hbmFileName", _hbmFileName);
            context.put("system", staticModels.get("java.lang.System"));
            context.put("packagePath", PropsValues.PACKAGE_PATH);
            context.put("serviceBuilder", this);
            context.put("tempMap", wrapper.wrap(new HashMap<String, Object>()));
            return context;
    }

    private String _processTemplate(String name) throws Exception {
		return _processTemplate(name, _getContext());
	}

	private String _processTemplate(String name, Map<String, Object> context) throws Exception {
		return FreeMarkerUtil.process(name, context);
	}

    public String getPrimitiveJavaType(String type){
        if(type.equals("Date")){
            return "java.util.Date";
        }else{
            return type;
        }
    }
    public String getPrimitiveObj(String type) {
		if (type.equals("boolean")) {
			return "java.lang.Boolean";
		}
		else if (type.equals("double")) {
			return "java.lang.Double";
		}
		else if (type.equals("float")) {
			return "java.lang.Float";
		}
		else if (type.equals("int")) {
			return "java.lang.Integer";
		}
		else if (type.equals("long")) {
			return "java.lang.Long";
		}
		else if (type.equals("short")) {
			return "java.lang.Short";
		}else if (type.equals("String")) {
			return "java.lang.String";
		}
		else {
			return type;
		}
	}
    public String getPrimitiveObjClass(String type) {
		if (type.equals("boolean")) {
			return "Boolean";
		}
		else if (type.equals("double")) {
			return "Double";
		}
		else if (type.equals("float")) {
			return "Float";
		}
		else if (type.equals("int")) {
			return "Integer";
		}
		else if (type.equals("long")) {
			return "Long";
		}
		else if (type.equals("short")) {
			return "Short";
		}
		else {
			return type;
		}
	}
    public Object getPrimitiveObjValue(String type, String value) {
		if (type.equals("boolean")) {
			return Boolean.parseBoolean(value);
		}
		else if (type.equals("double")) {
			return Double.parseDouble(value);
		}
		else if (type.equals("float")) {
			return Float.parseFloat(value);
		}
		else if (type.equals("int")) {
			return Integer.parseInt(value);
		}
		else if (type.equals("long")) {
			return Long.parseLong(value);
		}
		else if (type.equals("short")) {
			return Short.parseShort(value);
		}
//		else if (type.equals("Date")) {
//            try{
//                DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                return dfm.parse(value);
//            }catch(ParseException pe){
//                return new Date();
//            }catch(Exception e){
//                return new Date();
//            }
//            return new Date();
//		}
        else {
			return "\""+value+"\"";
		}
	}
    public String getGeneratorClass(String idType) {
		if (Validator.isNull(idType)) {
			idType = "assigned";
		}
		return idType;
	}

    public String getXmlFileName(String ejbName) throws Exception {
        _hbmFileName = PropsValues.GENERATE_DIR_PATH + ejbName + ".hbm.xml";
        File xmlFile = new File(_hbmFileName);
        if (xmlFile.exists()) {
            return _hbmFileName;
        } else {
            return "";
        }
   }
}
