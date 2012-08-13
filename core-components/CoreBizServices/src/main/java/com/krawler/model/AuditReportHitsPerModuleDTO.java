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
package com.krawler.model;

/**
 * @author Johnson 
 * A Data Transfer Object used for report generation. Will hold
 *         the data for the number of hits per module.
 */
public class AuditReportHitsPerModuleDTO
{
    private String moduleName;
    private String hitCount;

    /**
     * @return the moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @param moduleName
     *            the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @return the hitCount
     */
    public String getHitCount()
    {
        return hitCount;
    }

    /**
     * @param hitCount
     *            the hitCount to set
     */
    public void setHitCount(String hitCount)
    {
        this.hitCount = hitCount;
    }

}
