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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.MVEL;


/**
 * This util file will be providing APIs for expressions parsing, validation and evaluation.
 * 
 * @author Ashutosh
 *
 */
public class ExpressionManagerImpl implements ExpressionManager
{
    private static final Log LOG = LogFactory.getLog(ExpressionManagerImpl.class);
    
    /* (non-Javadoc)
     * @see com.krawler.baseline.crm.bizservices.el.ExpressionManager#validateExpression(java.lang.String)
     */
    public boolean validateExpression(String expression)
    {
        boolean result = true;
        try
        {
            MVEL.compileExpression(expression);
        } catch (Exception e)
        {
            LOG.info("Can't compile the expression:" + expression, e);
            result = false;
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.baseline.crm.bizservices.el.ExpressionManager#evaluateExpression(java.lang.String, java.util.Map)
     */
    public Object evaluateExpression(String expression, Map<String, Object> variablesMap) throws Exception
    {
        Object result = null;
        
        try  {
            result = MVEL.eval(expression, variablesMap);
        } catch (Exception e) {
            LOG.info("Can't evaluate the expression:" + expression, e);
            throw e;
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.baseline.crm.bizservice.el.ExpressionManager#evaluateExpression(java.lang.String, java.util.List)
     */
    public Object evaluateExpression(String expression, String key, Map<String, ExpressionVariables> variables)
    {
        Object result = null;
        if (variables != null && !variables.isEmpty())
        {
            Object compiledExpression = MVEL.compileExpression(expression);

            for (ExpressionVariables variable : variables.values())
            {
                try
                {
                    result = MVEL.executeExpression(compiledExpression, variable.getVariablesMapForFormulae());
                    if(result instanceof Double){
                    	Double d = (Double)result;
                    	if(d==null||d.isInfinite()||d.isNaN())
                    		result=null;
                    }
                    variable.getOutputMap().put(key, result);
                } catch (Exception e)
                {
                    LOG.info("Can't evaluate the expression:" + expression, e);
                }
            }
        }
        return result;
    }
}
