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

import java.util.Properties;

/**
 * <a href="SafeProperties.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class SafeProperties extends Properties {

	public SafeProperties() {
		super();
	}

	public synchronized Object get(Object key) {
		Object value = super.get(key);

		value = _decode((String)value);

		return value;
	}

	public String getEncodedProperty(String key) {
		return super.getProperty(key);
	}

	public String getProperty(String key) {
		return (String)get(key);
	}

	public synchronized Object put(Object key, Object value) {
		if (key == null) {
			return null;
		}
		else {
			if (value == null) {
				return super.remove(key);
			}
			else {
				value = _encode((String)value);

				return super.put(key, value);
			}
		}
	}

	public synchronized Object remove(Object key) {
		if (key == null) {
			return null;
		}
		else {
			return super.remove(key);
		}
	}

	private static String _decode(String value) {
		return StringUtil.replace(
			value, _SAFE_NEWLINE_CHARACTER, StringPool.NEW_LINE);
	}

	private static String _encode(String value) {
		return StringUtil.replace(
			value,
			new String[] {
				StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE,
				StringPool.RETURN
			},
			new String[] {
				_SAFE_NEWLINE_CHARACTER, _SAFE_NEWLINE_CHARACTER,
				_SAFE_NEWLINE_CHARACTER
			});
	}

	private static final String _SAFE_NEWLINE_CHARACTER =
		"_SAFE_NEWLINE_CHARACTER_";

}
