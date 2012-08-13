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

package com.krawler.br.exp;

import com.krawler.br.ProcessException;
import com.krawler.br.modules.ModuleBag;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;

/**
 *
 * @author krawler-user
 */
public class SessionScope implements Scope {
    private sessionHandlerImpl sessionObject;

    public void setSessionObject(sessionHandlerImpl sessionObject) {
        this.sessionObject = sessionObject;
    }

    @Override
    public Object getScopeValue(String key) throws ProcessException {
        return sessionObject.getAttribute(key);
    }

    @Override
    public void setScopeValue(String key, Object val) throws ProcessException {
        sessionObject.setAttribute(key, val);
    }

    @Override
    public void removeScopeValue(String key) throws ProcessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getIdentity() {
        return "s";
    }

    @Override
    public String getScopeModuleName(String key) {
        return ModuleBag.PRIMITIVE.STRING.tagName();
    }

}
