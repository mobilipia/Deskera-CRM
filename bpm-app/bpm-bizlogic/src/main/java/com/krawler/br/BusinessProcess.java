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

package com.krawler.br;

import com.krawler.br.operations.OperationParameter;
import java.util.Map;

/**
 * Interface for process execution
 *
 * @author Vishnu Kant Gupta
 */
public interface BusinessProcess {
    /**
     * convenient function to execute the process.
     * @param params parameters required to execute the process. these should
     * match with the arguments described in definition of the process.
     * @return result object of process execution, generally a hashmap containing
     * key-value pair of result for different operations performed.
     * @throws java.lang.Throwable
     */
    public Object execute(Map params) throws ProcessException;
    public Map<String, OperationParameter> getInputParams();

    public String getView();
}
