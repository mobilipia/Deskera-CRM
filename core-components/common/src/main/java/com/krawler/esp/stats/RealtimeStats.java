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
package com.krawler.esp.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.krawler.common.util.ArrayUtil;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;

/**
 * This implementation of <code>Accumulator</code> is used to retrieve the
 * current value of a statistic. When a system component initializes, it calls
 * {@link #setCallback}
 */
class RealtimeStats implements Accumulator {

	private List<String> mNames;
	private List<RealtimeStatsCallback> mCallbacks = new ArrayList<RealtimeStatsCallback>();

	RealtimeStats(String[] names) {
		if (ArrayUtil.isEmpty(names)) {
			throw new IllegalArgumentException("names cannot be null or empty");
		}
		mNames = new ArrayList<String>();
		for (String name : names) {
			mNames.add(name);
		}
	}

	void addName(String name) {
		mNames.add(name);
	}

	void addCallback(RealtimeStatsCallback callback) {
		mCallbacks.add(callback);
	}

	public List<String> getNames() {
		return mNames;
	}

	public List<Object> getData() {
		List<Object> data = new ArrayList<Object>();

		// Collect stats from all callbacks
		Map<String, Object> callbackResults = new HashMap<String, Object>();
		for (RealtimeStatsCallback callback : mCallbacks) {
			Map<String, Object> callbackData = callback.getStatData();
			if (callbackData != null) {
				callbackResults.putAll(callbackData);
			}
		}

		// Populate data based on callback results
		for (String name : mNames) {
			data.add(callbackResults.remove(name));
		}
		if (callbackResults.size() > 0) {
			KrawlerLog.perf.warn("Detected unexpected realtime stats: "
					+ StringUtil.join(", ", callbackResults.keySet()));

		}
		return data;
	}

	public void reset() {
	}
}
