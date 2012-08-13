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
package com.krawler.spring.crm.common;

import com.krawler.spring.chartXmlSettings.*;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public class chartXmlSettingController extends MultiActionController {

    private chartXmlSettingDAO chartXmlSettingDAOObj;
    private String successView;

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setchartXmlSettingDAO(chartXmlSettingDAO chartXmlSettingDAOObj1) {
        this.chartXmlSettingDAOObj = chartXmlSettingDAOObj1;
    }

    public ModelAndView getBarChartSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        String retStr = "";
        try {
            String unit = request.getParameter("unit");
            String unitPosition = request.getParameter("unitposition");
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String rotateChart = request.getParameter("rotate");
            String graphColor = request.getParameter("color");
            String doubleBar = request.getParameter("doublebar");
            String title1 = request.getParameter("title1");
            String title2 = request.getParameter("title2");
            String show_legend = request.getParameter("show_legend");
            
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("title", !StringUtil.isNullOrEmpty(year)?title+"("+year+")":title);
            requestParams.put("unit", StringUtil.checkForNull(unit));
            requestParams.put("unitposition", StringUtil.checkForNull(unitPosition));
            requestParams.put("rotatechart", StringUtil.checkForNull(rotateChart));
            requestParams.put("graphcolor", StringUtil.checkForNull(graphColor));
            requestParams.put("doublebar", StringUtil.checkForNull(doubleBar));
            requestParams.put("title1", StringUtil.checkForNull(title1));
            requestParams.put("title2", StringUtil.checkForNull(title2));
            if(!StringUtil.isNullOrEmpty(show_legend)) {
                requestParams.put("show_legend", Boolean.parseBoolean(show_legend));
            }

            kmsg = chartXmlSettingDAOObj.getBarChartXML(requestParams);
            retStr = (String) kmsg.getEntityList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("chartView", "model", retStr);
    }

    public ModelAndView getPieChartSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String retStr = "";
        KwlReturnObject kmsg = null;
        try {
            String title = request.getParameter("title");
            String hideBalloonValue = request.getParameter("hide_balloon_value");
            String groupPercent = request.getParameter("group_percent");
            String year = request.getParameter("year");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("title", !StringUtil.isNullOrEmpty(year)?title+"("+year+")":title);
            if(!StringUtil.isNullOrEmpty(groupPercent)){
                requestParams.put("group_percent", groupPercent);
            }    
            if(!StringUtil.isNullOrEmpty(hideBalloonValue)) {
                requestParams.put("hide_balloon_value", Boolean.parseBoolean(hideBalloonValue));
            }
            kmsg = chartXmlSettingDAOObj.getPieChartXML(requestParams);
            retStr = (String) kmsg.getEntityList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("chartView", "model", retStr);
    }
}
