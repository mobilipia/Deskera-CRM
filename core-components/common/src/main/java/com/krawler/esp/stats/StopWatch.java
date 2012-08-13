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

/**
 * A <code>Counter</code> that supports <code>start()</code> and
 * <code>stop()</code> methods for conveniently timing events. By default, the
 * count and average times are logged and the total is not.
 */
public class StopWatch extends Counter {

	StopWatch(String name) {
		super(name, "ms");
		setShowCount(true);
		setShowAverage(true);
		setShowTotal(false);
	}

	public long start() {
		return System.currentTimeMillis();
	}

	public void stop(long startTime) {
		increment(System.currentTimeMillis() - startTime);
	}
}
