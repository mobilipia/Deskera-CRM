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

import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.SourceParser;
import java.util.Map;

/**
 * Interface to provide the parsing facility for operation definition
 *
 * @author Vishnu Kant Gupta
 */
public interface OperationDefinitionParser extends SourceParser {

    /**
     * parses and returns the list of successfully parsed operation definition
     *
     * @param loaders supplied loaders to be used
     * @return array containing operation definitions
     */
    OperationDefinition[] parse(Map loaders, SourceFactory src);
}
