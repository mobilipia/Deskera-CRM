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

public class UnicodeFormatter {

	public static char HEX_DIGIT[] = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	public static String byteToHex(byte b) {
		char[] array = {HEX_DIGIT[(b >> 4) & 0x0f], HEX_DIGIT[b & 0x0f]};

		return new String(array);
	}

	public static String charToHex(char c) {
		byte hi = (byte)(c >>> 8);
		byte lo = (byte)(c & 0xff);

		return byteToHex(hi) + byteToHex(lo);
	}

	public static String parseString(String hexString) {
		StringBuilder sb = new StringBuilder();

		char[] array = hexString.toCharArray();

		if ((array.length % 6) != 0) {
//			_log.error("String is not in hex format");

			return hexString;
		}

		for (int i = 2; i < hexString.length(); i = i + 6) {
			String s = hexString.substring(i, i + 4);

			try {
				char c = (char)Integer.parseInt(s, 16);

				sb.append(c);
			}
			catch (Exception e) {
//				_log.error(e, e);

				return hexString;
			}
		}

		return sb.toString();
	}

	public static String toString(char[] array) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append("\\u");
			sb.append(charToHex(array[i]));
		}

		return sb.toString();
	}

	public static String toString(String s) {
		if (s == null) {
			return null;
		}

		return toString(s.toCharArray());
	}

//	private static Log _log = LogFactoryUtil.getLog(UnicodeFormatter.class);

}
