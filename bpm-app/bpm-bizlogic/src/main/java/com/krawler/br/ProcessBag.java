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

import com.krawler.br.utils.SourceFactory;
import java.util.Set;

/**
 * Interface to mimic a bag of business processes from which different business
 * processes can be build and extracted for execution
 *
 * @author Vishnu Kant Gupta
 */
public interface ProcessBag {
    /**
     * convenient function to construct the business process.
     * @param parser a node parser to parse the process bag to find and construct
     * the process
     * @param id the id of process to construct
     * @return the business process
     * @throws com.krawler.br.InvalidFlowException if can not construct the process
     * because of the flow in the given definition is not correct.
     * @throws com.krawler.br.ProcessException if process can not be constructed
     * due to some exceptional cases.
     */
    public BusinessProcess getProcess(SourceFactory src, String id) throws ProcessException;

    public void addProcess(SourceFactory src, BusinessProcess process) throws ProcessException;

    public void removeProcess(SourceFactory src, String id) throws ProcessException;
    /**
     * provides the names of all available processes defined
     *
     * @return set of all process names
     */
    public Set getProcessIDs(SourceFactory src);
}
