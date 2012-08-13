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
package com.krawler.spring.common;


import com.krawler.calendar.calendarmodule.CalendarDao;
import com.krawler.spring.common.commonDAO;
import java.util.List;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class CommonController  extends MultiActionController {
    private commonDAO commonDAOObj;
    private String successView;

    public String getSuccessView() {
		return successView;
	}
    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setcommonDAO(commonDAO commonDAOObj) {
        this.commonDAOObj = commonDAOObj;
    }

    public ModelAndView gettimezone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
//        List result = null;
        String result = "";
        try {
            result = commonDAOObj.gettimezone(request);
        } catch(Exception e) {
            System.out.print(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }

    public ModelAndView getcountry(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
//        List result = null;
        String result = "";
        try {
            result = commonDAOObj.getcountry(request);
        } catch(Exception e) {
            System.out.print(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }
    
    public ModelAndView getsharingdata(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
//        List result = null;
        String result = "";
        try {
            result = commonDAOObj.getsharingdata(request);
        } catch(Exception e) {
            System.out.print(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", result);
    }
    

}
