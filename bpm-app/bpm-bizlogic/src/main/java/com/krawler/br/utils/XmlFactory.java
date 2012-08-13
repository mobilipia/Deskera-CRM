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

package com.krawler.br.utils;

import com.krawler.br.ProcessException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlFactory implements HierarchicalSourceFactory {
    public static final String PUBLIC_ID = "-//KRAWLER//DTD BUSINESSRULES//EN";
    public static final String SYSTEM_ID = "http://192.168.0.4/dtds/businesslogicEx.dtd";
    private Document doc; //XML document to be load
    private DomUtil util = new DomUtil(); //Common XML Dom utilities
    private SourceParser parser;
    private Properties props;
    private SourceFactory parentSourceFactory;

    public XmlFactory(Properties sourceProp, SourceParser parser) throws ProcessException {
        this(sourceProp, parser, null);
    }

    public XmlFactory(Properties sourceProp, SourceParser parser, SourceFactory parentSourceFactory) throws ProcessException {
        this.parentSourceFactory = parentSourceFactory;
        this.props = (Properties)sourceProp.clone();
        this.doc = prepareDoc(getFile(sourceProp));
        this.parser = parser;
    }

   /**
     * getter for the XML document attached with this factory
     * @return XML document
     */
    public Document getDocument(){
        return doc;
    }

    /**
     * getter for the common dom utilities to be used while parsing
     * @return common dom utilities
     */
    public DomUtil getDomUtil(){
        return util;
    };

    protected Document prepareDoc(File file) throws ProcessException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db=null;
        //dbf.setValidating(true);

        try {
            db = dbf.newDocumentBuilder();
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
        try {
            return db.parse(file);
        } catch (Exception ex) {
            return db.newDocument();
        }
    }

    private File getFile(Properties prop) {
        try {
            if (prop.getProperty("filesystempath") != null) {
                return new FileSystemResource(prop.getProperty("filesystempath")).getFile();
            } else {
                return new File(new ClassPathResource("").getFile(),prop.getProperty("path"));
            }
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public void save() throws ProcessException {
        save(props);
    }

    @Override
    public void save(Properties sourceProp) throws ProcessException {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, SYSTEM_ID);
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, PUBLIC_ID);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            File f = getFile(sourceProp);
            if(!f.exists()){
                f.createNewFile();
            }

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }

    @Override
    public SourceParser getParser() {
        return this.parser;
    }

    @Override
    public boolean containsLocal(String sourceid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SourceFactory getParentSourceFactory() {
        return parentSourceFactory;
    }

    @Override
    public Set getIDs() {
        Set ids=parser.getIDs(this);
        if(parentSourceFactory!=null){
            Set temp = parentSourceFactory.getIDs();
            ids.addAll(temp);
        }
        return ids;
    }

    @Override
    public Properties getProps() {
        return this.props;
    }
}
