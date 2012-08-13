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

package com.krawler.portal.util;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <a href="Time.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class Time {

	public static final long SECOND = 1000;

	public static final long MINUTE = SECOND * 60;

	public static final long HOUR = MINUTE * 60;

	public static final long DAY = HOUR * 24;

	public static final long WEEK = DAY * 7;

	public static final String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

	public static final String TIMESTAMP_FORMAT = "yyyyMMddkkmmssSSS";

	public static final String SHORT_TIMESTAMP_FORMAT = "yyyyMMddkkmm";

	public static Date getDate(Calendar cal) {
		Calendar adjustedCal = Calendar.getInstance();
		adjustedCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		adjustedCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		adjustedCal.set(Calendar.DATE, cal.get(Calendar.DATE));
		adjustedCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		adjustedCal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		adjustedCal.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		adjustedCal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));

		return adjustedCal.getTime();
	}

	public static Date getDate(TimeZone tz) {
		Calendar cal = Calendar.getInstance(tz);

		return getDate(cal);
	}

	public static Date getDate(Date date, TimeZone tz) {
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(date);

		return getDate(cal);
	}

	public static String getDescription(long milliseconds) {
		String s = "";

		int x = 0;

		if (milliseconds % WEEK == 0) {
			x = (int)(milliseconds / WEEK);

			s = x + " Week";
		}
		else if (milliseconds % DAY == 0) {
			x = (int)(milliseconds / DAY);

			s = x + " Day";
		}
		else if (milliseconds % HOUR == 0) {
			x = (int)(milliseconds / HOUR);

			s = x + " Hour";
		}
		else if (milliseconds % MINUTE == 0) {
			x = (int)(milliseconds / MINUTE);

			s = x + " Minute";
		}
		else if (milliseconds % SECOND == 0) {
			x = (int)(milliseconds / SECOND);

			s = x + " Second";
		}

		if (x > 1) {
			s += "s";
		}

		return s;
	}

	public static String getRFC822() {
		return getRFC822(new Date());
	}

	public static String getRFC822(Date date) {
		return getSimpleDate(date, RFC822_FORMAT);
	}

	public static String getShortTimestamp() {
		return getShortTimestamp(new Date());
	}

	public static String getShortTimestamp(Date date) {
		return getSimpleDate(date, SHORT_TIMESTAMP_FORMAT);
	}

	public static String getSimpleDate(Date date, String format) {
		String s = StringPool.BLANK;

		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);

			s = sdf.format(date);
		}

		return s;
	}

	public static String getTimestamp() {
		return getTimestamp(new Date());
	}

	public static String getTimestamp(Date date) {
		return getSimpleDate(date, TIMESTAMP_FORMAT);
	}

}
