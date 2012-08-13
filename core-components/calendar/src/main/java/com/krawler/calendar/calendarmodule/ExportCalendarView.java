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
package com.krawler.calendar.calendarmodule;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;

import org.springframework.web.servlet.view.AbstractView;

public class ExportCalendarView extends AbstractView {
	private String filename;
	public ExportCalendarView() {
		super();
		this.filename = "iCalDeskEra.ics";
	}

	public ExportCalendarView(String filename) {
		super();
		this.filename = filename;
	}
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=" + this.filename + ";");
		Calendar cal = (Calendar)model.get("calendar");
		
        PropertyList props =  cal.getProperties();
        props.add(new ProdId("-//Krawler Inc//Deskera//EN"));
        props.add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
        props.add(CalScale.GREGORIAN);
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(cal, response.getOutputStream());
	}

	@Override
	public String getContentType() {
		return "text/plain";
	}
}
