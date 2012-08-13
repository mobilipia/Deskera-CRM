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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common DOM utilities to be used while parsing any XML file
 * @author Vishnu Kant Gupta
 */
public class DomUtil {
    /**
     * function to get the list of immidiate child elements of the element supplied
     * elements will be filtered if a tag name will be supplied.
     * @param el element whose direct children to be determined.
     * @param tagName tag name of child elements or null if all elements
     * @return list of elements if any, or empty list if no element exists or the
     * given element is null.
     */
    public synchronized List<Element> getChildElements(Element el, String tagName){
        List<Element> list = new ArrayList<Element>();
        if(el==null) return list;
        NodeList nl = el.getChildNodes();
        for(int i=0; i<nl.getLength();i++){
            Node n = nl.item(i);
            if(n instanceof Element){
                if(tagName!=null){
                    if(tagName.equals(n.getNodeName()))
                        list.add((Element)n);
                }else
                    list.add((Element)n);
            }
        }
        return list;
    }

    /**
     * function to get the list of immidiate child elements of the element supplied
     * @param el element whose direct children to be determined.
     * @return list of elements if any, or empty list if no element exists or the
     * given element is null.
     */
    public synchronized List<Element> getChildElements(Element el){
        return getChildElements(el, null);
    }

    /**
     * function to get the first of immidiate child elements of the element supplied
     * or null if no such element exists
     * elements will be filtered if a tag name will be supplied.
     * @param el element whose direct children to be determined.
     * @param tagName tag name of child elements or null if all elements
     * @return element if any, or null if no element exists or the
     * given element is null.
     */
    public synchronized Element getChildElement(Element el, String tagName){
        List<Element> l=getChildElements(el, tagName);
        if(l.isEmpty())
            return null;
        return l.get(0);
    }

    public List filterList(String attrName, String attrValue, List<Element> nl){
        Iterator<Element> itr = nl.iterator();
        List<Element> list = new ArrayList<Element>();
        while(itr.hasNext()){
            Element e=itr.next();
            if(e.getAttribute(attrName).equals(attrValue))
                list.add(e);
        }
        return list;
    }
}
