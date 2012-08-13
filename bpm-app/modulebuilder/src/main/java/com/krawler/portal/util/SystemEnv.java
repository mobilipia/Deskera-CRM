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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemEnv {
    private static final Log logger = LogFactory.getLog(SystemEnv.class);

	public static Properties getProperties() {
		Properties props = new Properties();

		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = null;

			String osName = System.getProperty("os.name").toLowerCase();

			if (osName.indexOf("windows ") > -1) {
				if (osName.indexOf("windows 9") > -1) {
					process = runtime.exec("command.com /c set");
				}
				else {
					process = runtime.exec("cmd.exe /c set");
				}
			}
			else {
				process = runtime.exec("env");
			}

			BufferedReader br = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

			String line;

			while ((line = br.readLine()) != null) {
				int pos = line.indexOf(StringPool.EQUAL);

				if (pos != -1) {
					String key = line.substring(0, pos);
					String value = line.substring(pos + 1);

					props.setProperty(key, value);
				}
			}
		}
		catch (IOException ioe) {
			logger.warn(ioe.getMessage(), ioe);
		}

		return props;
	}

	public static void setProperties(Properties props) {
		Properties envProps = getProperties();

		Enumeration<String> enu = (Enumeration<String>)envProps.propertyNames();

		while (enu.hasMoreElements()) {
			String key = enu.nextElement();

			props.setProperty("env." + key, (String)envProps.get(key));
		}
	}

}
