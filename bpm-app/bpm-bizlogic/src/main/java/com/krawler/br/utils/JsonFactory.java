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
import com.krawler.utils.json.base.JSONObject;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class JsonFactory implements HierarchicalSourceFactory {
    private JSONObject json; //XML document to be load
    private SourceParser parser;
    private SourceFactory parentSourceFactory;
    private Properties props;

    public JsonFactory(Properties sourceProp, SourceParser parser) throws ProcessException {
        this.json = prepareJson(sourceProp);
        this.parser = parser;
    }

    public JsonFactory(Properties sourceProp, SourceParser parser, SourceFactory parentSourceFactory) throws ProcessException {
        this.parentSourceFactory = parentSourceFactory;
        this.json = prepareJson(sourceProp);
        this.parser = parser;
        this.props = (Properties) sourceProp.clone();
    }
    /**
     * getter for the XML document attached with this factory
     * @return XML document
     */
    public JSONObject getJSONObject(){
        return json;
    }

    protected JSONObject prepareJson(Properties prop) throws ProcessException {
        try {
            return new JSONObject(prop.getProperty("json"));
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }      
    }

    @Override
    public void save() throws ProcessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(Properties sourceProp) throws ProcessException {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return parser.getIDs(this);
    }

    @Override
    public Properties getProps() {
        return this.props;
    }
}
