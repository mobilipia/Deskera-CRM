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
package com.krawler.esp.service.util;

import java.util.HashMap;
import java.util.Map;

import com.krawler.common.util.KrawlerLog;

/**
 * Maintains per-thread data, used to keep track of performance statistics and
 * other data that pertains to a single request. Methods in this class are not
 * thread-safe, since each thread will be synchronously updating its own set of
 * data.
 */
public class ThreadLocalData {

	private static final String TL_START_TIME = "StartTime";
	private static final String TL_DB_TIME = "DbTime";
	private static final String TL_STATEMENT_COUNT = "SqlStatementCount";

	private static ThreadLocal sThreadLocal = new ThreadLocal();

	/**
	 * Resets all per-thread data.
	 */
	public static void reset() {
		Map map = getThreadLocalMap();
		map.clear();
		map.put(TL_START_TIME, new Long(System.currentTimeMillis()));
	}

	/**
	 * Returns the number of milliseconds elapsed since {@link #reset} was last
	 * called.
	 */
	public static long getProcessingTime() {
		Map map = getThreadLocalMap();
		Long startTime = (Long) map.get(TL_START_TIME);
		if (startTime == null) {
			KrawlerLog.perf.warn("", new IllegalStateException(
					"getProcessingTime() called before reset()"));
			return 0;
		}
		return System.currentTimeMillis() - startTime.longValue();
	}

	/**
	 * Returns the total database execution time in milliseconds for the current
	 * thread.
	 */
	public static int getDbTime() {
		Integer i = (Integer) getThreadLocalMap().get(TL_DB_TIME);
		if (i == null) {
			return 0;
		}
		return i.intValue();
	}

	/**
	 * Returns the number of SQL statements that have been executed for the
	 * current thread.
	 */
	public static int getStatementCount() {
		Integer i = (Integer) getThreadLocalMap().get(TL_STATEMENT_COUNT);
		if (i == null) {
			return 0;
		}
		return i.intValue();
	}

	/**
	 * Increments this thread's database execution time and statement count.
	 */
	public static void incrementDbTime(int millis) {
		// Increment database time
		Integer i = (Integer) getThreadLocalMap().get(TL_DB_TIME);
		int previousValue = 0;
		if (i != null) {
			previousValue = i.intValue();
		}
		i = new Integer(previousValue + millis);
		getThreadLocalMap().put(TL_DB_TIME, i);

		// Increment statement count
		i = (Integer) getThreadLocalMap().get(TL_STATEMENT_COUNT);
		previousValue = 0;
		if (i != null) {
			previousValue = i.intValue();
		}
		i = new Integer(previousValue + 1);
		getThreadLocalMap().put(TL_STATEMENT_COUNT, i);
	}

	private static Map getThreadLocalMap() {
		Map map = (Map) sThreadLocal.get();
		if (map == null) {
			map = new HashMap();
			sThreadLocal.set(map);
		}
		return map;
	}
}
