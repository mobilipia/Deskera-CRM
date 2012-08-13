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
package com.krawler.common.stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StatUtil {

    public static final DateFormat DATE_FORMAT_GMT = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss" );

    static
    {
        DATE_FORMAT_GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

	/**
	 * Returns the current time formatted as <tt>MM/dd/yyyy hh:mm:ss</tt>.
	 */
	public static String getTimestampString() {
		return String.format("%1$tm/%1$td/%1$tY %1$tH:%1$tM:%1$tS", new Date());
	}
}
