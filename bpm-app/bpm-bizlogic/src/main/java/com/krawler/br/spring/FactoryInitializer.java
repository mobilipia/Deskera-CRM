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

package com.krawler.br.spring;

import com.krawler.br.operations.SimpleOperationBag;
import com.krawler.br.modules.SimpleModuleBag;
import com.krawler.br.utils.SourceFactory;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class FactoryInitializer {
    private SimpleModuleBag moduleBag;
    private SimpleOperationBag operationBag;
    private SourceFactory moduleSourceFactory;
    private SourceFactory operationSourceFactory;

    public SimpleModuleBag getModuleBag() {
        return moduleBag;
    }

    public void setModuleBag(SimpleModuleBag moduleBag) {
        this.moduleBag = moduleBag;
    }

    public SourceFactory getModuleSourceFactory() {
        return moduleSourceFactory;
    }

    public void setModuleSourceFactory(SourceFactory moduleSourceFactory) {
        this.moduleSourceFactory = moduleSourceFactory;
    }

    public SimpleOperationBag getOperationBag() {
        return operationBag;
    }

    public void setOperationBag(SimpleOperationBag operationBag) {
        this.operationBag = operationBag;
    }

    public SourceFactory getOperationSourceFactory() {
        return operationSourceFactory;
    }

    public void setOperationSourceFactory(SourceFactory operationSourceFactory) {
        this.operationSourceFactory = operationSourceFactory;
    }

    public void init(){
        moduleBag.load(moduleSourceFactory);
        operationBag.addHelpers();
        operationBag.load(operationSourceFactory);
    }
}
