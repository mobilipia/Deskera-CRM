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
import java.util.List;
import java.util.Locale;

import com.krawler.common.util.StringUtil;

/**
 * <code>Accumulator</code> implementation that keeps track of a total and
 * number of values (count) for the given statistic.
 */
public class Counter implements Accumulator {

	private volatile long mCount = 0;
	private volatile long mTotal = 0;
	private boolean mShowCount = false;
	private boolean mShowTotal = true;
	private boolean mShowAverage = false;
	private String mCountName;
	private String mTotalName;
	private String mAverageName;

	Counter(String name, String units) {
		mCountName = name + "_count";
		if (StringUtil.isNullOrEmpty(units)) {
			mTotalName = name;
			mAverageName = name + "_avg";
		} else {
			mTotalName = name + "_" + units;
			mAverageName = name + "_" + units + "_avg";
		}
	}

	Counter(String name) {
		this(name, null);
	}

	public long getCount() {
		return mCount;
	}

	public long getTotal() {
		return mTotal;
	}

	public double getAverage() {
		if (mCount == 0) {
			return 0.0;
		} else {
			return (double) mTotal / (double) mCount;
		}
	}

	/**
	 * If <code>true</code>, the count of values will be logged in a column
	 * called <code>[name]_count</code>. The default is <code>false</code>.
	 */
	public void setShowCount(boolean showCount) {
		mShowCount = showCount;
	}

	/**
	 * Sets the name of the count column. The default name is
	 * <code>name_count</code>.
	 */
	public void setCountName(String countName) {
		mCountName = countName;
	}

	/**
	 * Sets the name of the total column. The default name is
	 * <code>name[_units]</code>.
	 */
	public void setTotalName(String totalName) {
		mTotalName = totalName;
	}

	/**
	 * Sets the name of average column. The default name is
	 * <code>name[_units]_avg</code>.
	 */
	public void setAverageName(String averageName) {
		mAverageName = averageName;
	}

	/**
	 * If <code>true</code>, the total of values will be logged in a column
	 * called <code>[name]</code>. The default is <code>true</code>.
	 */
	public void setShowTotal(boolean showTotal) {
		mShowTotal = showTotal;
	}

	/**
	 * If <code>true</code>, the average of values will be logged in a column
	 * called <code>[name]_avg</code>. The default is <code>false</code>.
	 */
	public void setShowAverage(boolean showAverage) {
		mShowAverage = showAverage;
	}

	/**
	 * Increments the total by the specified value. Increments the count by 1.
	 */
	public synchronized void increment(long value) {
		mCount++;
		mTotal += value;
	}

	/**
	 * Increments the count and total by 1.
	 */
	public synchronized void increment() {
		increment(1);
	}

	public synchronized void reset() {
		mCount = 0;
		mTotal = 0;
	}

	public List<String> getNames() {
		List<String> labels = new ArrayList<String>();
		if (mShowTotal) {
			labels.add(mTotalName);
		}
		if (mShowCount) {
			labels.add(mCountName);
		}
		if (mShowAverage) {
			labels.add(mAverageName);
		}
		return labels;
	}

	public synchronized List<Object> getData() {
		List<Object> data = new ArrayList<Object>();
		if (mShowTotal) {
			if (mCount > 0) {
				data.add(mTotal);
			} else {
				data.add("");
			}
		}
		if (mShowCount) {
			if (mCount > 0) {
				data.add(mCount);
			} else {
				data.add("");
			}
		}
		if (mShowAverage) {
			if (mCount > 0) {
				// Force US locale, so that the file format is the same for all
				// locales
				// and we don't run into problems with commas.
				data.add(String.format(Locale.US, "%.2f", getAverage()));
			} else {
				data.add("");
			}
		}
		return data;
	}

	protected int getNumColumns() {
		int numColumns = 0;
		if (mShowTotal) {
			numColumns++;
		}
		if (mShowCount) {
			numColumns++;
		}
		if (mShowAverage) {
			numColumns++;
		}
		return numColumns;
	}
}
