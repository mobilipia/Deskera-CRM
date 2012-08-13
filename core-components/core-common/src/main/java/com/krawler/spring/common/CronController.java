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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.krawler.esp.utils.CompanyRoutingDataSource;

public class CronController extends AbstractController {
	private CompanyRoutingDataSource routingDataSource;

	public void setRoutingDataSource(CompanyRoutingDataSource routingDataSource) {
		this.routingDataSource = routingDataSource;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		StringBuffer url= request.getRequestURL();
		int idx = url.indexOf("/cron/");
		URL u = new URL(url.replace(idx, idx+5, "").toString());
		StringBuffer pramsStr = new StringBuffer();
		Map params = request.getParameterMap();
		for(Object e:params.entrySet()){
			Map.Entry param = (Map.Entry)e;
			String key =(String)param.getKey();
			String[] values = (String[])param.getValue();
			for(String val:values){
				appendParam(pramsStr, key, val);
			}
		}
		
		if(request.getParameter("cdomain")!=null&&request.getParameter("cdomain").length()>0){
			callURL(u, pramsStr);
		}else{
			String pStr = pramsStr.toString();
			Collection<String> subdomains =  routingDataSource.getOneCompanyPerDataSource();
			for(String subdomain: subdomains){
				StringBuffer buff = new StringBuffer(pStr);
				if(request.getParameter("cdomain")==null||request.getParameter("cdomain").length()<=0)
					appendParam(buff, "cdomain", subdomain);

				callURL(u, buff);
			}
		}
		return new ModelAndView("jsonView","model","{success:true}");
	}
	
	private void appendParam(StringBuffer url,String key, String value) throws UnsupportedEncodingException{
		StringBuffer paramStr= new StringBuffer();
		paramStr.append(URLEncoder.encode(key,"UTF-8")).append("=").append(URLEncoder.encode(value,"UTF-8"));
		if(url.length()>0){
			url.append("&");
		}
		url.append(paramStr);
	}
	
	private void callURL(URL url, StringBuffer params){
		java.io.BufferedReader in=null;
		
        try{               
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
            pw.println(params);
            pw.close();
            try {
                in = new java.io.BufferedReader(new java.io.InputStreamReader(uc.getInputStream()));
                String res = URLDecoder.decode(in.readLine(),"UTF-8");
            } catch(Exception ex){
                
            }
            in.close();
        } catch (IOException ex) {
            logger.warn("Diversion not possible for "+url+" ["+params+"]",ex);                
        } finally {
        	if(in!=null)
				try {in.close();} catch (IOException e) {}
        }
	}
}
