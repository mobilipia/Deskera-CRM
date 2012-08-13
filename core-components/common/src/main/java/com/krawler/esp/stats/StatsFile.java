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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.krawler.esp.service.util.ThreadLocalData;
import com.krawler.esp.utils.ConfigReader;

/**
 * Represents a stats file in CSV format. Stats files are fact tables, written
 * by {@link KrawlerPerf#writeEventStats} that store statistical data about
 * individual events, such as processing time, database time, and the number of
 * SQL statements that the event generated.
 */
public class StatsFile {

	private boolean mLogThreadLocal;
	private String mFilename;
	private String[] mStatNames = new String[0];
	private File mFile;

	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat(
			"yyyyMMdd-HHmm");

	/**
	 * @param filename
	 *            the name of this <code>StatsFile</code>. The actual
	 *            filename stored in {@link LC#log_directory} is
	 *            <code>[name]-[timestamp].csv</code>.
	 * @param statNames
	 *            the names of any extra stat columns, or <code>null</code> if
	 *            none
	 * @param logThreadLocal
	 *            <code>true</code> if statistics from {@link ThreadLocalData}
	 *            should be logged
	 */
	public StatsFile(String name, String[] statNames, boolean logThreadLocal) {
		mFilename = name + "-" + TIMESTAMP_FORMATTER.format(new Date())
				+ ".csv";
		String logDir = ConfigReader.getinstance().get("log_directory", "log");
		mFile = new File(logDir + "/" + mFilename);
		if (statNames != null) {
			mStatNames = statNames;
		}
		mLogThreadLocal = logThreadLocal;
	}

	String getFilename() {
		return mFilename;
	}

	File getFile() {
		return mFile;
	}

	/**
	 * @return the stat names, or an empty array if none are defined
	 */
	String[] getStatNames() {
		return mStatNames;
	}

	boolean shouldLogThreadLocal() {
		return mLogThreadLocal;
	}
}
