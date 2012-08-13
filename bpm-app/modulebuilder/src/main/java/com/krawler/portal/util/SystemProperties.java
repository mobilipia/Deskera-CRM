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

import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class SystemProperties {
        private static final Log logger=LogFactory.getLog(SystemProperties.class);
	public static final String SYSTEM_PROPERTIES_LOAD =
		"system.properties.load";

	public static final String SYSTEM_PROPERTIES_FINAL =
		"system.properties.final";

	public static final String TMP_DIR = "java.io.tmpdir";

	public static String get(String key) {
		String value = _instance._props.get(key);

		if (value == null) {
			value = System.getProperty(key);
		}

		return value;
	}

	public static void set(String key, String value) {
		System.setProperty(key, value);

		_instance._props.put(key, value);
	}

	public static String[] getArray(String key) {
		String value = get(key);

		if (value == null) {
			return new String[0];
		}
		else {
			return StringUtil.split(value);
		}
	}

	public static Properties getProperties() {
		return PropertiesUtil.fromMap(_instance._props);
	}

	private SystemProperties() {
		Properties p = new Properties();

		ClassLoader classLoader = getClass().getClassLoader();

		// system.properties

		try {
			URL url = classLoader.getResource("system.properties");

			if (url != null) {
				InputStream is = url.openStream();

				p.load(is);

				is.close();

				logger.debug("Loading " + url);
			}
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		// system-ext.properties

		try {
			URL url = classLoader.getResource("system-ext.properties");

			if (url != null) {
				InputStream is = url.openStream();

				p.load(is);

				is.close();

				logger.debug("Loading " + url);
			}
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		// Set environment properties

		SystemEnv.setProperties(p);

		// Set system properties

		boolean systemPropertiesLoad = GetterUtil.getBoolean(
			System.getProperty(SYSTEM_PROPERTIES_LOAD), true);

		boolean systemPropertiesFinal = GetterUtil.getBoolean(
			System.getProperty(SYSTEM_PROPERTIES_FINAL), true);

		if (systemPropertiesLoad) {
			Enumeration<String> enu = (Enumeration<String>)p.propertyNames();

			while (enu.hasMoreElements()) {
				String key = enu.nextElement();

				if (systemPropertiesFinal ||
					Validator.isNull(System.getProperty(key))) {

					System.setProperty(key, p.getProperty(key));
				}
			}
		}

		_props = new ConcurrentHashMap<String, String>();

		// Use a fast concurrent hash map implementation instead of the slower
		// java.util.Properties

		PropertiesUtil.fromProperties(p, _props);
	}

	private static SystemProperties _instance = new SystemProperties();

	private Map<String, String> _props;

}
