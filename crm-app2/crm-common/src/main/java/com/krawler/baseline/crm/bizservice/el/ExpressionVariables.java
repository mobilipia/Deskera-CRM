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
package com.krawler.baseline.crm.bizservice.el;

import java.util.HashMap;
import java.util.Map;

/**
 * Object for storing expression input variables and expression output
 * @author Ashutosh
 *
 */
public class ExpressionVariables
{
    private Map<String, Object> variablesMap;

    private Map<String, Object> variablesMapForFormulae;
    
    private Map<String, Object> outputMap = new HashMap<String, Object>();

    public Map<String, Object> getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(Map<String, Object> outputMap) {
        this.outputMap = outputMap;
    }

    public Map<String, Object> getVariablesMap()
    {
        return variablesMap;
    }

    public void setVariablesMap(Map<String, Object> variablesMap)
    {
        this.variablesMap = variablesMap;
    }

    public Map<String, Object> getVariablesMapForFormulae() {
        return variablesMapForFormulae;
    }

    public void setVariablesMapForFormulae(Map<String, Object> variablesMapForFormulae) {
        this.variablesMapForFormulae = variablesMapForFormulae;
    }
}
