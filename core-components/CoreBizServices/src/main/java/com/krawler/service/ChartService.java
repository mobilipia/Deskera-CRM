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

import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;
/**
 *
 * @author krawler
 */
public class ChartService implements IChartService {
    
    private int MIN_PIE_CHARTCOUNT = 10;
    private int MIN_BAR_CHARTCOUNT = 10;
    private String Others ="Others";
    private String CHART ="<chart>";

    private String SERIES ="<series>";
    private String graphs ="<graphs><graph gid=\"0\">";
    private String END_CHART ="</chart>";

    private String END_SERIES ="</series>";
    private String END_GRAPHS ="</graph></graphs>";

    private String PIE ="<pie>";
    private String END_PIE ="</pie>";
    private String VALUE ="<value xid=\"";
    private String END_VALUE ="</value>";
    private String END_GT ="\" >";
    private String SLICE ="<slice title=\"";
    private String END_SLICE ="</slice>";
    
    private StringBuilder getPieChartSlice(Object[] record){
            StringBuilder slice = new StringBuilder();
            slice.append(SLICE);
            slice.append(record[2]);
            slice.append(END_GT);
            slice.append(record[0]);
            slice.append(END_SLICE);
            return slice;
    }
    private StringBuilder appendBarChartGraph(StringBuilder graph,Object[] record){
            graph.append(VALUE);
            graph.append(record[1]);
            graph.append(END_GT);
            graph.append(record[0]);
            graph.append(END_VALUE);
            return graph;
    }
    private void appendBarChartSeries(StringBuilder series,Object[] record){
            series.append(VALUE);
            series.append(record[1]);
            series.append(END_GT);
            series.append(record[2]);
            series.append(END_VALUE);
    }
    @Override
    public StringBuilder getPieChart(List<Object[]> ll) {
            StringBuilder chart = new StringBuilder(PIE);
            int otherCount =0,count=0;
            for(Object[] record:ll){
                if(count < MIN_PIE_CHARTCOUNT){
                    chart.append(getPieChartSlice(record));
                    count++;
                }else
                   otherCount+=Integer.parseInt(record[0].toString());
            }
            if(otherCount > 0){
                    Object[] record = {otherCount,Others,Others};
                    chart.append(getPieChartSlice(record));
            }
            chart.append(END_PIE);
            return chart;
    }

    @Override
    public JSONArray getPieChartJson(List<Object[]> ll) throws JSONException {
        JSONArray jArray = new JSONArray();
        StringBuilder chart = new StringBuilder(PIE);
        int otherCount = 0, count = 0;
        for (Object[] record : ll) {
            if (count < MIN_PIE_CHARTCOUNT) {
                JSONObject tempObj = new JSONObject();
                tempObj.put("name", record[2]);
                tempObj.put("value", record[0]);
                jArray.put(tempObj);
                count++;
            } else {
                otherCount += Integer.parseInt(record[0].toString());
            }
        }
        if (otherCount > 0) {
            JSONObject tempObj = new JSONObject();
            tempObj.put("name", Others);
            tempObj.put("value", otherCount);
            jArray.put(tempObj);
        }
        chart.append(END_PIE);
        return jArray;
    }
    
    @Override
    public StringBuilder getBarChart(List<Object[]> ll) {
            StringBuilder chart = new StringBuilder(CHART);
            StringBuilder series = new StringBuilder(SERIES);
            StringBuilder graph = new StringBuilder(graphs);
            int otherCount =0,count=0;
            for(Object[] record:ll){
                if(count < MIN_BAR_CHARTCOUNT){
                    appendBarChartSeries(series,record);
                    appendBarChartGraph(graph,record);
                    count++;
                }else
                otherCount+=Integer.parseInt(record[0].toString());
            }
            if(otherCount > 0){
                    Object[] record = {otherCount,Others,Others};
                    appendBarChartSeries(series,record);
                    appendBarChartGraph(graph,record);
            }
            series.append(END_SERIES);
            graph.append(END_GRAPHS);
            chart.append(series);
            chart.append(graph);
            chart.append(END_CHART);
            return chart;
    }

    

}
