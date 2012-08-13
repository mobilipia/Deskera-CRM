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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <a href="PropertiesUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class PropertiesUtil {

	public static void copyProperties(Properties from, Properties to) {
		Iterator itr = from.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			to.setProperty((String)entry.getKey(), (String)entry.getValue());
		}
	}

	public static Properties fromMap(Map map) {
		if (map instanceof Properties) {
			return (Properties)map;
		}

		Properties p = new Properties();

		Iterator itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			String key = (String)entry.getKey();
			String value = (String)entry.getValue();

			if (value != null) {
				p.setProperty(key, value);
			}
		}

		return p;
	}

	public static void fromProperties(Properties p, Map map) {
		map.clear();

		Iterator itr = p.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			map.put(entry.getKey(), entry.getValue());
		}
	}

	public static Properties load(String s) throws IOException {
		Properties p = new Properties();

		load(p, s);

		return p;
	}

	public static void load(Properties p, String s) throws IOException {
		if (Validator.isNotNull(s)) {
			s = UnicodeFormatter.toString(s);

			s = StringUtil.replace(s, "\\u003d", "=");
			s = StringUtil.replace(s, "\\u000a", "\n");
			s = StringUtil.replace(s, "\\u0021", "!");
			s = StringUtil.replace(s, "\\u0023", "#");
			s = StringUtil.replace(s, "\\u0020", " ");
			s = StringUtil.replace(s, "\\u005c", "\\");

			p.load(new ByteArrayInputStream(s.getBytes()));

			List propertyNames = Collections.list(p.propertyNames());

			for (int i = 0; i < propertyNames.size(); i++) {
				String key = (String)propertyNames.get(i);

				String value = p.getProperty(key);

				// Trim values because it may leave a trailing \r in certain
				// Windows environments. This is a known case for loading SQL
				// scripts in SQL Server.

				if (value != null) {
					value = value.trim();

					p.setProperty(key, value);
				}
			}
		}
	}

	public static void merge(Properties p1, Properties p2) {
		Enumeration enu = p2.propertyNames();

		while (enu.hasMoreElements()) {
			String key = (String)enu.nextElement();
			String value = p2.getProperty(key);

			p1.setProperty(key, value);
		}
	}

	public static String list(Map map) {
		Properties props = fromMap(map);

		ByteArrayMaker bam = new ByteArrayMaker();
		PrintStream ps = new PrintStream(bam);

		props.list(ps);

		return bam.toString();
	}

	public static void list(Map map, PrintStream out) {
		Properties props = fromMap(map);

		props.list(out);
	}

	public static void list(Map map, PrintWriter out) {
		Properties props = fromMap(map);

		props.list(out);
	}

	public static String toString(Properties p) {
		SafeProperties safeProperties = null;

		if (p instanceof SafeProperties) {
			safeProperties = (SafeProperties)p;
		}

		StringBuilder sb = new StringBuilder();

		Enumeration enu = p.propertyNames();

		while (enu.hasMoreElements()) {
			String key = (String)enu.nextElement();

			sb.append(key);
			sb.append(StringPool.EQUAL);

			if (safeProperties != null) {
				sb.append(safeProperties.getEncodedProperty(key));
			}
			else {
				sb.append(p.getProperty(key));
			}

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	public static void trimKeys(Properties p) {
		Enumeration enu = p.propertyNames();

		while (enu.hasMoreElements()) {
			String key = (String)enu.nextElement();
			String value = p.getProperty(key);

			String trimmedKey = key.trim();

			if (!key.equals(trimmedKey)) {
				p.remove(key);
				p.setProperty(trimmedKey, value);
			}
		}
	}

}
