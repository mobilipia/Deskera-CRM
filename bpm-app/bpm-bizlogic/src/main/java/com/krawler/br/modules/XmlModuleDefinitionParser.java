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

package com.krawler.br.modules;

import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * Module definition parser from a given XML file
 *
 * @author Vishnu Kant Gupta
 */
public class XmlModuleDefinitionParser implements ModuleDefinitionParser {

    @Override
    public Set getIDs(SourceFactory src) {
        XmlFactory factory = (XmlFactory)src;
        List<Element> list = factory.getDomUtil().getChildElements(factory.getDocument().getDocumentElement(), ELEMENT.MODULE.tagName());
        HashSet mds=new HashSet();
        for(int i=0;i<list.size();i++){
                mds.add(list.get(i).getAttribute(ATTRIBUTE.M_ID.tagName()));
        }
        return mds;
    }

    enum ELEMENT{
        ROOT("modules"),
        MODULE("module"),
        PROPERTYLIST("property-list"),
        PROPERTY("property");
        ELEMENT(String tag) {
            this.tag=tag;
        }
        public String tagName(){
            return tag;
        }
        private final String tag;
    }

    enum ATTRIBUTE{
        M_ID("id"),
        M_TYPE("type"),
        M_CLASS("class"),
        P_TYPE("type"),
        P_NAME("name"),
        P_MULTI("multi");

        ATTRIBUTE(String tag) {
            this.tag=tag;
        }
        public String tagName(){
            return tag;
        }
        private final String tag;
    }

    public ModuleDefinition[] parse(SourceFactory src) {
        XmlFactory factory = (XmlFactory)src;
        List<Element> list = factory.getDomUtil().getChildElements(factory.getDocument().getDocumentElement(), ELEMENT.MODULE.tagName());
        ArrayList mds=new ArrayList();
        for(int i=0;i<list.size();i++){
                ModuleDefinition md=parse(factory,list.get(i));
                if(md!=null)mds.add(md);
        }
        return (ModuleDefinition[])mds.toArray(new ModuleDefinition[0]);
    }

    /**
     * parses the module definition represented by the given DOM element
     *
     * @param el DOM element to be parsed
     * @return module definition or null if element doesn't represent an module
     * definition
     */
    public ModuleDefinition parse(XmlFactory factory,Element el){
        ModuleDefinition md=null;
        if(el.getTagName().equals(ELEMENT.MODULE.tagName())){
            md= new SimpleModuleDefinition();
            md.setName(el.getAttribute(ATTRIBUTE.M_ID.tagName()));
            String mType=el.getAttribute(ATTRIBUTE.M_TYPE.tagName());
            for(ModuleDefinition.TYPE t:ModuleDefinition.TYPE.values())
                if(t.tagName().equals(mType))
                    md.setType(t);
            md.setClassName(el.getAttribute(ATTRIBUTE.M_CLASS.tagName()));
            List<Element> list = factory.getDomUtil().getChildElements(
                    factory.getDomUtil().getChildElement(el, ELEMENT.PROPERTYLIST.tagName()),
                    ELEMENT.PROPERTY.tagName());
            
            for(int i=0;i<list.size();i++){
                ModuleProperty mp=parseProperty(list.get(i));
                if(mp!=null)md.addProperty(mp);
            }
        }
        return md;
    }

    /**
     * parses the module property represented by the given DOM element
     *
     * @param el DOM element to be parsed
     * @return module property or null if element doesn't represent an module
     * property
     */
    public ModuleProperty parseProperty(Element el) {
        ModuleProperty mp=null;
        if(el.getTagName().equals(ELEMENT.PROPERTY.tagName())){
            mp = new SimpleModuleProperty();
            mp.setName(el.getAttribute(ATTRIBUTE.P_NAME.tagName()));
            mp.setType(el.getAttribute(ATTRIBUTE.P_TYPE.tagName()));
            String multi=el.getAttribute(ATTRIBUTE.P_MULTI.tagName());
            for(ModuleProperty.MULTI m:ModuleProperty.MULTI.values())
                if(m.tagName().equals(multi))
                    mp.setMulti(m);
        }
        return mp;
    }
}
