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

package com.krawler.br.operations;

import com.krawler.br.loader.KwlLoader;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.utils.XmlFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * Operation definition parser from a given XML file
 *
 * @author Vishnu Kant Gupta
 */
public class XmlOperationDefinitionParser implements OperationDefinitionParser {

    @Override
    public Set getIDs(SourceFactory src) {
        XmlFactory factory = (XmlFactory)src;
        List<Element> list = factory.getDomUtil().getChildElements(factory.getDocument().getDocumentElement(), ELEMENT.OPERATION.tagName());
        HashSet ads=new HashSet();
        for(int i=0;i<list.size();i++){
            ads.add(list.get(i).getAttribute(ATTRIBUTE.A_ID.tagName()));
        }
        return ads;
    }
    /**
     * all elements to describe operation definition
     */
    public enum ELEMENT{
        ROOT("operations"),          // root element
        OPERATION("operation"),       // operation element, under the root
        OPERATEDBY("entity"),  // operated by element under operation
        INVARS("in-vars"),          // invars element under operation
        INVAR("in-var"),            // in var element under invars
        OUTVAR("out-var");          // out var element under operation
        ELEMENT(String tag) {
            this.tag=tag;
        }
        public String tagName(){
            return tag;
        }
        private final String tag;
    }

    /**
     * different attribute for diffrent elements
     */
    public enum ATTRIBUTE{
        A_ID("id"),                     // id of operation
        A_METHOD_NAME("method-name"),                 // method name of operation definition
        A_NAME("name"),  // display name of operation definition
        O_TYPE("type"),                 // operated by type (loader type)
        O_LOADER("name"),             // operated by name (loader name)
        I_TYPE("module"),                 // variable type
        I_NAME("name"),                 // variable name
        I_MULTI("multi"),               // multi type for variable
        I_INDEX("index");               // variable index

        ATTRIBUTE(String tag) {
            this.tag=tag;
        }
        public String tagName(){
            return tag;
        }
        private final String tag;
    }

    @Override
    public OperationDefinition[] parse(Map loaders, SourceFactory src) {
        XmlFactory factory = (XmlFactory)src;
        List<Element> list = factory.getDomUtil().getChildElements(factory.getDocument().getDocumentElement(), ELEMENT.OPERATION.tagName());
        ArrayList ads=new ArrayList();
        for(int i=0;i<list.size();i++){
            OperationDefinition ad=parse(factory, list.get(i),loaders);
            if(ad!=null)ads.add(ad);
        }
        return (OperationDefinition[])ads.toArray(new OperationDefinition[0]);
    }

    /**
     * parses the operation definition represented by the given DOM element
     *
     * @param el DOM element to be parsed
     * @return operation definition or null if element doesn't represent an operation
     * definition
     */
    public OperationDefinition parse(XmlFactory factory, Element el, Map loaders) {
        OperationDefinition ad=null;
        if(el.getTagName().equals(ELEMENT.OPERATION.tagName())){
            ad= new SimpleOperationDefinition();
            ad.setName(el.getAttribute(ATTRIBUTE.A_NAME.tagName()));
            ad.setID(el.getAttribute(ATTRIBUTE.A_ID.tagName()));
            ad.setMethodName(el.getAttribute(ATTRIBUTE.A_METHOD_NAME.tagName()));
            Element loader = factory.getDomUtil().getChildElement(el,ELEMENT.OPERATEDBY.tagName());
            ad.setEntityName(loader.getAttribute(ATTRIBUTE.O_LOADER.tagName()));
            String lType=loader.getAttribute(ATTRIBUTE.O_TYPE.tagName());
                    ad.setEntityLoader((KwlLoader)loaders.get(lType));

            ad.setOutputParameter(parseParams(factory.getDomUtil().getChildElement(el,ELEMENT.OUTVAR.tagName())));
            List<Element> list = factory.getDomUtil().getChildElements(factory.getDomUtil().getChildElement(el,ELEMENT.INVARS.tagName()),ELEMENT.INVAR.tagName());

            OperationParameter[] aps=new OperationParameter[list.size()];
            ArrayList exaps=new ArrayList();
            for(int i=0;i<list.size();i++){
                Element apEl = list.get(i);
                OperationParameter ap=parseParams(apEl);
                if(ap!=null){
                    try {
                        int index = Integer.parseInt(apEl.getAttribute(ATTRIBUTE.I_INDEX.tagName()));
                        aps[index]=ap;
                    } catch (NumberFormatException ex) {
                        exaps.add(ap);
                    }
                }
            }
            for(int i=0;i<aps.length;i++){
                if(exaps.isEmpty())break;
                if(aps[i]==null){
                    aps[i]=(OperationParameter)exaps.remove(0);
                }
            }
            ad.setInputParameters(aps);
        }
        return ad;
    }

    /**
     * parses the operation parameter represented by the given DOM element
     * 
     * @param el DOM element to be parsed
     * @return operation parameter or null if element doesn't represent an operation
     * parameter
     */
    public OperationParameter parseParams(Element el) {
        OperationParameter ap=null;
        if(el!=null&&(el.getTagName().equals(ELEMENT.INVAR.tagName())||el.getTagName().equals(ELEMENT.OUTVAR.tagName()))){
            ap = new SimpleOperationParameter();
            ap.setName(el.getAttribute(ATTRIBUTE.I_NAME.tagName()));
            ap.setType(el.getAttribute(ATTRIBUTE.I_TYPE.tagName()));
            String multi=el.getAttribute(ATTRIBUTE.I_MULTI.tagName());
            for(ModuleProperty.MULTI m:ModuleProperty.MULTI.values())
                if(m.tagName().equals(multi))
                    ap.setMulti(m);
        }
        return ap;
    }
}
