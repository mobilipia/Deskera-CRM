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

public interface ExpressionManager
{

    /**
     * This API is used for validating an expression
     * 
     * @param expression Expression
     * @return true if valid else false
     */
    boolean validateExpression(String expression);

    /**
     * This API is used for evaluating an expression using the variables map passed
     * 
     * @param expression Expression
     * @param variablesMap Variable Map
     * @return evaluated output
     */
    Object evaluateExpression(String expression, Map<String, Object> variablesMap) throws Exception;
    
    /**
     * API for evaluation an expression for multiple variable maps
     * 
     * @param expression
     * @key key
     * @param variables
     */
    Object evaluateExpression(String expression, String key, Map<String, ExpressionVariables> variables);

}
