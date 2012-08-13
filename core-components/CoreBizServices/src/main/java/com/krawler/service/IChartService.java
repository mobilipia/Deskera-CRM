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

package com.krawler.service;


import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import java.util.List;

/**
 *
 * @author krawler
 */
public interface  IChartService {

    /**
     *
     * @param ll - must contain Array of objects, Each Object Array length must be 3 where Objectarr[0]=count  Objectarr[1]=id Objectarr[2]=name
     * @return StringBuilder containing String required to display chart
     */
    StringBuilder getPieChart(List<Object[]> ll);

    /**
     *
     * @param ll - must contain Array of objects, Each Object Array length must be 3 where Objectarr[0]=count  Objectarr[1]=id Objectarr[2]=name
     * @return StringBuilder containing String required to display chart
     * @throws JSONException 
     */
    JSONArray getPieChartJson(List<Object[]> ll) throws JSONException;
    
    /**
     *
     * @param ll - must contain Array of objects, Each Object Array length must be 3 where Objectarr[0]=count  Objectarr[1]=id Objectarr[2]=name
     * @return StringBuilder containing String required to display chart
     */
    StringBuilder getBarChart(List<Object[]> ll);
}
