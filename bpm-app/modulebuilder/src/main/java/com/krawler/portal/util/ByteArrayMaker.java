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

import java.io.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="ByteArrayMaker.java.html"><b><i>View Source</i></b></a>
 *
 * @author Harry Mark
 *
 */
public class ByteArrayMaker extends ByteArrayOutputStream {

	static boolean collect = false;
        private static final Log logger = LogFactory.getLog(ByteArrayMaker.class);

	static {
		String collectString = System.getProperty(MakerStats.class.getName());

		if (collectString != null) {
			if (collectString.equals("true")) {
				collect = true;
			}
		}
	}

	static MakerStats stats = null;

	static {
		if (collect) {
			stats = new MakerStats(ByteArrayMaker.class.toString());
		}
	}

	static int defaultInitSize = 8000;

	static {
		String defaultInitSizeString = System.getProperty(
			ByteArrayMaker.class.getName() + ".initial.size");

		if (defaultInitSizeString != null) {
			try {
				defaultInitSize = Integer.parseInt(defaultInitSizeString);
			}
			catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

	public static MakerStats getStatistics() {
		return stats;
	}

	public ByteArrayMaker() {
		super(defaultInitSize);

		if (collect) {
			_getInfo(new Throwable());
		}
	}

	public ByteArrayMaker(int size) {
		super(size);

		if (collect) {
			_getInfo(new Throwable());
		}
	}

	public byte[] toByteArray() {
		if (collect) {
			stats.add(_caller, _initSize, count);
		}

		return super.toByteArray();
	}

	public String toString() {
		return super.toString();
	}

	private void _getInfo(Throwable t) {
		_initSize = buf.length;

		StackTraceElement[] elements = t.getStackTrace();

		if (elements.length > 1) {
			StackTraceElement el = elements[1];

			_caller =
				el.getClassName() + StringPool.PERIOD + el.getMethodName() +
					StringPool.COLON + el.getLineNumber();
		}
	}

	private int _initSize;
	private String _caller;

}
