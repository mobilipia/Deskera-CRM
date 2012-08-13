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

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="MakerStats.java.html"><b><i>View Source</i></b></a>
 *
 * @author Harry Mark
 *
 */
public class MakerStats {

	public MakerStats(String name) {
		//_name = name;
	}

	public void add(String caller, int initSize, int finalSize) {
		SizeSample stat = null;

		synchronized (_map) {
			stat = _map.get(caller);

			if (stat == null) {
				stat = new SizeSample(caller, initSize);

				_map.put(caller, stat);
			}

			_count++;
		}

		synchronized (stat) {
			stat.add(finalSize);
		}
	}

	public void display(PrintStream printer) {
		printer.println("caller,min,max,range,samples,average,initial");

		List<SizeSample> list = new ArrayList<SizeSample>(_map.size());

		list.addAll(_map.values());

		list = ListUtil.sort(list);

		int maxSize = 0;
		int sampleSize = 0;
		int totalSize = 0;

		for (int i = 0; i < list.size(); i++) {
			SizeSample stat = list.get(i);

			printer.print(stat.getCaller());
			printer.print(",");
			printer.print(stat.getMinSize());
			printer.print(",");
			printer.print(stat.getMaxSize());
			printer.print(",");
			printer.print(stat.getMaxSize() - stat.getMinSize());
			printer.print(",");
			printer.print(stat.getSamplesSize());
			printer.print(",");
			printer.print(stat.getTotalSize() / stat.getSamplesSize());
			printer.print(",");
			printer.println(stat.getInitSize());

			sampleSize += stat.getSamplesSize();
			totalSize += stat.getTotalSize();

			if (stat.getMaxSize() > maxSize) {
				maxSize = stat.getMaxSize();
			}
		}

		int avg = 0;

		if (sampleSize > 0) {
			avg = totalSize / sampleSize;
		}

		printer.print("SAMPLES=");
		printer.print(sampleSize);
		printer.print(", AVERAGE=");
		printer.print(avg);
		printer.print(", MAX=");
		printer.println(maxSize);
	}

	//private String _name;
	private Map<String, SizeSample> _map = new HashMap<String, SizeSample>();
	private int _count;

	private class SizeSample implements Comparable<SizeSample> {

		public SizeSample(String caller, int initSize) {
			_caller = caller;
			_initSize = initSize;
			_minSize = Integer.MAX_VALUE;
			_maxSize = Integer.MIN_VALUE;
		}

		public void add(int finalSize) {
			if (finalSize < _minSize) {
				_minSize = finalSize;
			}

			if (finalSize > _maxSize) {
				_maxSize = finalSize;
			}

			_samplesSize++;
			_totalSize += finalSize;
		}

		public String getCaller() {
			return _caller;
		}

		public int getInitSize() {
			return _initSize;
		}

		public int getMaxSize() {
			return _maxSize;
		}

		public int getMinSize() {
			return _minSize;
		}

		public int getSamplesSize() {
			return _samplesSize;
		}

		public int getTotalSize() {
			return _totalSize;
		}

		public int compareTo(SizeSample other) {
			int thisAvg = 0;

			if (_samplesSize > 0) {
				thisAvg = _totalSize / _samplesSize;
			}

			int otherAvg = 0;

			if (other.getSamplesSize() > 0) {
				otherAvg = other.getTotalSize() / other.getSamplesSize();
			}

			return otherAvg - thisAvg;
		}

		private String _caller;
		private int _initSize;
		private int _maxSize;
		private int _minSize;
		private int _samplesSize;
		private int _totalSize;

	}

}
