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
import com.krawler.br.utils.JsonFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Module definition parser from a given XML file
 *
 * @author Vishnu Kant Gupta
 */
public class JsonModuleDefinitionParser implements ModuleDefinitionParser {

    @Override
    public Set getIDs(SourceFactory src) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    @Override
    public ModuleDefinition[] parse(SourceFactory src) {
        JsonFactory factory = (JsonFactory)src;
        JSONObject jobj = factory.getJSONObject();
        JSONArray jArr = factory.getJSONObject().optJSONArray(ELEMENT.ROOT.tagName());
        ArrayList mds=new ArrayList();
        if(jArr!=null){
            for(int i=0;i<jArr.length();i++){                
                try {
                    ModuleDefinition md = parse(factory, jArr.getJSONObject(i));
                    if(md!=null)mds.add(md);
                } catch (JSONException ex) {
                    Logger.getLogger(JsonModuleDefinitionParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
            }
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
    public ModuleDefinition parse(JsonFactory factory,JSONObject obj) throws JSONException{
        ModuleDefinition md=null;
        if(obj.getBoolean(ELEMENT.MODULE.tagName())){
            md= new SimpleModuleDefinition();
            md.setName(obj.getString(ATTRIBUTE.M_ID.tagName()));
            String mType=obj.getString(ATTRIBUTE.M_TYPE.tagName());
            for(ModuleDefinition.TYPE t:ModuleDefinition.TYPE.values())
                if(t.tagName().equals(mType))
                    md.setType(t);
            md.setClassName(obj.getString(ATTRIBUTE.M_CLASS.tagName()));
            JSONArray jArr = obj.getJSONArray(ELEMENT.PROPERTYLIST.tagName());
            
            for(int i=0;i<jArr.length();i++){
                ModuleProperty mp=parseProperty(jArr.getJSONObject(i));
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
    public ModuleProperty parseProperty(JSONObject obj) throws JSONException {
        ModuleProperty mp=null;
        if(obj.getBoolean(ELEMENT.PROPERTY.tagName())){
            mp = new SimpleModuleProperty();
            mp.setName(obj.getString(ATTRIBUTE.P_NAME.tagName()));
            mp.setType(obj.getString(ATTRIBUTE.P_TYPE.tagName()));
            String multi=obj.optString(ATTRIBUTE.P_MULTI.tagName());
            for(ModuleProperty.MULTI m:ModuleProperty.MULTI.values())
                if(m.tagName().equals(multi))
                    mp.setMulti(m);
        }
        return mp;
    }
}
